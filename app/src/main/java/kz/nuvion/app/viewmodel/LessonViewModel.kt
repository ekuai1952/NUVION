package kz.nuvion.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.Lesson
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

data class LessonContent(val topic: Topic, val lesson: Lesson, val completed: Boolean)

class LessonViewModel(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val topicId: String = savedStateHandle.get<String>(NavArgs.TOPIC_ID).orEmpty()

    private val content = MutableStateFlow<UiState<Pair<Topic, Lesson>>>(UiState.Loading)

    /** XP earned by the tap on "complete" (null until the lesson is completed in this visit). */
    private val _earnedXp = MutableStateFlow<Int?>(null)
    val earnedXp: StateFlow<Int?> = _earnedXp.asStateFlow()

    private val _isCompleting = MutableStateFlow(false)
    val isCompleting: StateFlow<Boolean> = _isCompleting.asStateFlow()

    val uiState: StateFlow<UiState<LessonContent>> = combine(
        content,
        progressRepository.safeProgress()
    ) { state, progress ->
        state.map { (topic, lesson) -> LessonContent(topic, lesson, topicId in progress.completedLessons) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    fun completeLesson() {
        val lesson = (content.value as? UiState.Success)?.data?.second ?: return
        if (_isCompleting.value) return
        viewModelScope.launch {
            _isCompleting.value = true
            try {
                _earnedXp.value = progressRepository.completeLesson(topicId, lesson.xpReward)
            } finally {
                _isCompleting.value = false
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            content.value = UiState.Loading
            content.value = loadState {
                val topic = contentRepository.getTopic(topicId)
                val lesson = contentRepository.getLesson(topicId)
                if (topic != null && lesson != null) topic to lesson else null
            }
            if (content.value is UiState.Success) progressRepository.setCurrentTopic(topicId)
        }
    }
}
