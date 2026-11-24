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
import kz.nuvion.app.domain.model.PracticeProblem
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.util.AnswerChecker
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

enum class AnswerStatus { NONE, CORRECT, WRONG, REVEALED }

data class PracticeSession(
    val index: Int = 0,
    val input: String = "",
    val inputError: Boolean = false,
    val hintVisible: Boolean = false,
    val status: AnswerStatus = AnswerStatus.NONE,
    val sessionXp: Int = 0
)

data class PracticeScreenData(
    val topic: Topic,
    val problems: List<PracticeProblem>,
    val session: PracticeSession,
    val solvedBefore: Boolean,
    val isBusy: Boolean
) {
    val total: Int get() = problems.size
    val problem: PracticeProblem get() = problems[session.index.coerceIn(0, total - 1)]
    val isLast: Boolean get() = session.index >= total - 1
    val showSolution: Boolean
        get() = session.status == AnswerStatus.CORRECT || session.status == AnswerStatus.REVEALED
    val canGoNext: Boolean get() = session.status != AnswerStatus.NONE && session.status != AnswerStatus.WRONG
    val progressFraction: Float
        get() = if (total == 0) 0f else (session.index + if (canGoNext) 1 else 0).toFloat() / total
}

sealed interface PracticeEvent {
    data class Finished(val topicId: String) : PracticeEvent
    data object SaveFailed : PracticeEvent
}

class PracticeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val topicId: String = savedStateHandle.get<String>(NavArgs.TOPIC_ID).orEmpty()

    private val content = MutableStateFlow<UiState<Pair<Topic, List<PracticeProblem>>>>(UiState.Loading)
    private val session = MutableStateFlow(
        PracticeSession(
            index = savedStateHandle[KEY_INDEX] ?: 0,
            input = savedStateHandle[KEY_INPUT] ?: "",
            hintVisible = savedStateHandle[KEY_HINT] ?: false,
            status = AnswerStatus.entries[savedStateHandle[KEY_STATUS] ?: 0],
            sessionXp = savedStateHandle[KEY_XP] ?: 0
        )
    )
    private val isBusy = MutableStateFlow(false)

    private val _events = Channel<PracticeEvent>(Channel.BUFFERED)
    val events: Flow<PracticeEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<UiState<PracticeScreenData>> = combine(
        content,
        session,
        isBusy,
        progressRepository.safeProgress()
    ) { state, s, busy, progress ->
        state.map { (topic, problems) ->
            val current = problems[s.index.coerceIn(0, problems.size - 1)]
            PracticeScreenData(topic, problems, s, progress.isSolved(topicId, current.id), busy)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    fun onInputChange(value: String) {
        if (session.value.status == AnswerStatus.CORRECT || session.value.status == AnswerStatus.REVEALED) return
        // Keep only characters that can form a number.
        val filtered = value.filter { it.isDigit() || it in ".,-−eE " }.take(MAX_INPUT)
        update { it.copy(input = filtered, inputError = false, status = AnswerStatus.NONE) }
    }

    fun toggleHint() = update { it.copy(hintVisible = !it.hintVisible) }

    fun check() {
        val problem = currentProblem() ?: return
        val s = session.value
        if (s.status == AnswerStatus.CORRECT || s.status == AnswerStatus.REVEALED || isBusy.value) return
        val value = AnswerChecker.parse(s.input)
        if (value == null) {
            update { it.copy(inputError = true) }
            return
        }
        if (AnswerChecker.isCorrect(value, problem.answer, problem.relativeTolerance)) {
            viewModelScope.launch {
                isBusy.value = true
                try {
                    val xp = progressRepository.markProblemSolved(topicId, problem.id, problem.xpReward)
                    update { it.copy(status = AnswerStatus.CORRECT, sessionXp = it.sessionXp + xp) }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    update { it.copy(status = AnswerStatus.CORRECT) }
                    _events.send(PracticeEvent.SaveFailed)
                } finally {
                    isBusy.value = false
                }
            }
        } else {
            update { it.copy(status = AnswerStatus.WRONG) }
        }
    }

    fun tryAgain() = update { it.copy(status = AnswerStatus.NONE, input = "") }

    fun revealSolution() = update { it.copy(status = AnswerStatus.REVEALED) }

    fun next() {
        val (_, problems) = (content.value as? UiState.Success)?.data ?: return
        val s = session.value
        if (s.status != AnswerStatus.CORRECT && s.status != AnswerStatus.REVEALED) return
        if (s.index >= problems.size - 1) {
            finish()
        } else {
            update { PracticeSession(index = it.index + 1, sessionXp = it.sessionXp) }
        }
    }

    private fun finish() {
        if (isBusy.value) return
        viewModelScope.launch {
            isBusy.value = true
            try {
                progressRepository.setLastReward(session.value.sessionXp)
                _events.send(PracticeEvent.Finished(topicId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(PracticeEvent.SaveFailed)
            } finally {
                isBusy.value = false
            }
        }
    }

    private fun currentProblem(): PracticeProblem? {
        val problems = (content.value as? UiState.Success)?.data?.second ?: return null
        return problems.getOrNull(session.value.index)
    }

    private fun update(transform: (PracticeSession) -> PracticeSession) {
        val s = transform(session.value)
        session.value = s
        savedStateHandle[KEY_INDEX] = s.index
        savedStateHandle[KEY_INPUT] = s.input
        savedStateHandle[KEY_HINT] = s.hintVisible
        savedStateHandle[KEY_STATUS] = s.status.ordinal
        savedStateHandle[KEY_XP] = s.sessionXp
    }

    private fun load() {
        viewModelScope.launch {
            content.value = UiState.Loading
            content.value = loadState {
                val topic = contentRepository.getTopic(topicId)
                val problems = contentRepository.getPracticeProblems(topicId)
                if (topic != null && problems.isNotEmpty()) topic to problems else null
            }
            if (content.value is UiState.Success) progressRepository.setCurrentTopic(topicId)
        }
    }

    private companion object {
        const val MAX_INPUT = 16
        const val KEY_INDEX = "practice_index"
        const val KEY_INPUT = "practice_input"
        const val KEY_HINT = "practice_hint"
        const val KEY_STATUS = "practice_status"
        const val KEY_XP = "practice_xp"
    }
}
