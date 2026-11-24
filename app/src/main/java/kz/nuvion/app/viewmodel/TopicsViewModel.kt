package kz.nuvion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

data class TopicItem(val topic: Topic, val progress: TopicProgress)

private data class TopicsContent(val topics: List<Topic>, val problemCounts: Map<String, Int>)

class TopicsViewModel(
    private val contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    private val content = MutableStateFlow<UiState<TopicsContent>>(UiState.Loading)

    val uiState: StateFlow<UiState<List<TopicItem>>> = combine(
        content,
        progressRepository.safeProgress()
    ) { state, progress ->
        state.map { data ->
            data.topics.map { topic ->
                TopicItem(topic, progress.topicProgress(topic.id, data.problemCounts[topic.id] ?: 0))
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
                val topics = contentRepository.getTopics().takeIf { it.isNotEmpty() }
                topics?.let { TopicsContent(it, contentRepository.problemCounts(it)) }
            }
        }
    }
}
