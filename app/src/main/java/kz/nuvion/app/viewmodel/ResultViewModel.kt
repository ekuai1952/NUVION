package kz.nuvion.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.NextStep
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.domain.model.nextStep
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.navigation.ResultType
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

data class ResultData(
    val type: ResultType,
    val topic: Topic,
    val score: Int,
    val total: Int,
    val passed: Boolean,
    val xpEarned: Int,
    val totalXp: Int,
    val topicProgress: TopicProgress
) {
    val nextStep: NextStep get() = topicProgress.nextStep()
}

private data class ResultContent(val topic: Topic, val questionCount: Int, val problemCount: Int)

class ResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val type: ResultType = ResultType.fromRoute(savedStateHandle.get<String>(NavArgs.TYPE))
    val topicId: String = savedStateHandle.get<String>(NavArgs.TOPIC_ID).orEmpty()

    private val content = MutableStateFlow<UiState<ResultContent>>(UiState.Loading)

    val uiState: StateFlow<UiState<ResultData>> = combine(
        content,
        progressRepository.safeProgress()
    ) { state, progress ->
        state.map { data ->
            val topicProgress = progress.topicProgress(topicId, data.problemCount)
            val quiz = progress.quizResults[topicId]
            when (type) {
                ResultType.QUIZ -> ResultData(
                    type, data.topic,
                    score = quiz?.lastScore ?: 0,
                    total = quiz?.total ?: data.questionCount,
                    passed = quiz?.passed == true,
                    xpEarned = progress.lastRewardXp,
                    totalXp = progress.totalXp,
                    topicProgress = topicProgress
                )
                ResultType.PRACTICE -> ResultData(
                    type, data.topic,
                    score = topicProgress.solvedProblems,
                    total = data.problemCount,
                    passed = topicProgress.solvedProblems >= data.problemCount,
                    xpEarned = progress.lastRewardXp,
                    totalXp = progress.totalXp,
                    topicProgress = topicProgress
                )
                ResultType.LESSON -> ResultData(
                    type, data.topic,
                    score = if (topicProgress.lessonCompleted) 1 else 0,
                    total = 1,
                    passed = topicProgress.lessonCompleted,
                    xpEarned = progress.lastRewardXp,
                    totalXp = progress.totalXp,
                    topicProgress = topicProgress
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            content.value = UiState.Loading
            content.value = loadState {
                contentRepository.getTopic(topicId)?.let { topic ->
                    ResultContent(
                        topic = topic,
                        questionCount = contentRepository.getQuiz(topicId)?.questions?.size ?: 0,
                        problemCount = contentRepository.getPracticeProblems(topicId).size
                    )
                }
            }
        }
    }
}
