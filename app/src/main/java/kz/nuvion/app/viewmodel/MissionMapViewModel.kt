package kz.nuvion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.Mission
import kz.nuvion.app.domain.model.MissionState
import kz.nuvion.app.domain.model.NextStep
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.nextStep
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.usecase.MissionCalculator
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

data class MissionMapData(
    val missions: List<Mission>,
    val totalXp: Int
) {
    val completedCount: Int get() = missions.count { it.state == MissionState.COMPLETED }
    val current: Mission? get() = missions.firstOrNull { it.state == MissionState.CURRENT }
    val nextStep: NextStep get() = current?.progress?.nextStep() ?: NextStep.DONE
    val fraction: Float get() = if (missions.isEmpty()) 0f else completedCount.toFloat() / missions.size
}

private data class MissionContent(val topics: List<Topic>, val problemCounts: Map<String, Int>)

class MissionMapViewModel(
    private val contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    private val content = MutableStateFlow<UiState<MissionContent>>(UiState.Loading)

    val uiState: StateFlow<UiState<MissionMapData>> = combine(
        content,
        progressRepository.safeProgress()
    ) { state, progress ->
        state.map { data ->
            MissionMapData(MissionCalculator.missions(data.topics, progress, data.problemCounts), progress.totalXp)
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
                topics?.let { MissionContent(it, contentRepository.problemCounts(it)) }
            }
        }
    }
}
