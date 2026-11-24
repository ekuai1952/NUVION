package kz.nuvion.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.Question
import kz.nuvion.app.domain.model.Quiz
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

/** Progress inside one quiz run; mirrored into SavedStateHandle so it survives process death. */
data class QuizSession(
    val index: Int = 0,
    val selectedOptionId: String? = null,
    val checked: Boolean = false,
    val correctCount: Int = 0
)

data class QuizScreenData(
    val topic: Topic,
    val quiz: Quiz,
    val session: QuizSession,
    val isSaving: Boolean
) {
    val total: Int get() = quiz.questions.size
    val question: Question get() = quiz.questions[session.index.coerceIn(0, total - 1)]
    val isLastQuestion: Boolean get() = session.index >= total - 1
    val answeredCount: Int get() = session.index + if (session.checked) 1 else 0
    val percent: Int get() = if (total == 0) 0 else answeredCount * 100 / total
    val isAnswerCorrect: Boolean get() = session.selectedOptionId == question.correctOptionId
}

sealed interface QuizEvent {
    data class Finished(val topicId: String) : QuizEvent
    data object SaveFailed : QuizEvent
}

class QuizViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val topicId: String = savedStateHandle.get<String>(NavArgs.TOPIC_ID).orEmpty()

    private val content = MutableStateFlow<UiState<Pair<Topic, Quiz>>>(UiState.Loading)
    private val session = MutableStateFlow(
        QuizSession(
            index = savedStateHandle[KEY_INDEX] ?: 0,
            selectedOptionId = savedStateHandle[KEY_SELECTED],
            checked = savedStateHandle[KEY_CHECKED] ?: false,
            correctCount = savedStateHandle[KEY_CORRECT] ?: 0
        )
    )
    private val isSaving = MutableStateFlow(false)

    private val _events = Channel<QuizEvent>(Channel.BUFFERED)
    val events: Flow<QuizEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<UiState<QuizScreenData>> = combine(content, session, isSaving) { state, s, saving ->
        state.map { (topic, quiz) -> QuizScreenData(topic, quiz, s, saving) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    fun selectOption(optionId: String) {
        if (session.value.checked) return
        update { it.copy(selectedOptionId = optionId) }
    }

    fun checkAnswer() {
        val data = currentData() ?: return
        val s = session.value
        if (s.checked || s.selectedOptionId == null) return
        val correct = s.selectedOptionId == data.question.correctOptionId
        update { it.copy(checked = true, correctCount = it.correctCount + if (correct) 1 else 0) }
    }

    fun next() {
        val data = currentData() ?: return
        if (!session.value.checked) return
        if (data.isLastQuestion) {
            finish(data)
        } else {
            update { it.copy(index = it.index + 1, selectedOptionId = null, checked = false) }
        }
    }

    private fun finish(data: QuizScreenData) {
        if (isSaving.value) return
        viewModelScope.launch {
            isSaving.value = true
            try {
                progressRepository.saveQuizResult(
                    topicId = topicId,
                    score = session.value.correctCount,
                    total = data.total,
                    passThreshold = data.quiz.passThreshold,
                    xpReward = data.quiz.xpReward
                )
                // The quiz screen is removed from the back stack on navigation, so the
                // session is not reset here (that would flash question 1 before leaving).
                _events.send(QuizEvent.Finished(topicId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(QuizEvent.SaveFailed)
            } finally {
                isSaving.value = false
            }
        }
    }

    private fun currentData(): QuizScreenData? =
        (content.value as? UiState.Success)?.data?.let { (topic, quiz) ->
            QuizScreenData(topic, quiz, session.value, isSaving.value)
        }

    private fun update(transform: (QuizSession) -> QuizSession) {
        val newSession = transform(session.value)
        session.value = newSession
        savedStateHandle[KEY_INDEX] = newSession.index
        savedStateHandle[KEY_SELECTED] = newSession.selectedOptionId
        savedStateHandle[KEY_CHECKED] = newSession.checked
        savedStateHandle[KEY_CORRECT] = newSession.correctCount
    }

    private fun load() {
        viewModelScope.launch {
            content.value = UiState.Loading
            content.value = loadState {
                val topic = contentRepository.getTopic(topicId)
                val quiz = contentRepository.getQuiz(topicId)?.takeIf { it.questions.isNotEmpty() }
                if (topic != null && quiz != null) topic to quiz else null
            }
            if (content.value is UiState.Success) progressRepository.setCurrentTopic(topicId)
        }
    }

    private companion object {
        const val KEY_INDEX = "quiz_index"
        const val KEY_SELECTED = "quiz_selected"
        const val KEY_CHECKED = "quiz_checked"
        const val KEY_CORRECT = "quiz_correct"
    }
}
