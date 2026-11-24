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
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.repository.SettingsRepository
import kz.nuvion.app.domain.usecase.MissionCalculator
import kz.nuvion.app.domain.usecase.ProfileCalculator
import kz.nuvion.app.domain.usecase.ProfileStats
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState
import kz.nuvion.app.util.map

data class ProfileData(
    val userName: String,
    val stats: ProfileStats,
    /** Lesson that "Continue learning" opens. */
    val continueTopicId: String?
)

private data class ProfileContent(val topics: List<Topic>, val problemCounts: Map<String, Int>)

class ProfileViewModel(
    private val contentRepository: ContentRepository,
    progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val content = MutableStateFlow<UiState<ProfileContent>>(UiState.Loading)

    val uiState: StateFlow<UiState<ProfileData>> = combine(
        content,
        progressRepository.safeProgress(),
        settingsRepository.settings
    ) { state, progress, settings ->
        state.map { data ->
            ProfileData(
                userName = settings.userName,
                stats = ProfileCalculator.stats(progress, data.topics, data.problemCounts),
                continueTopicId = MissionCalculator.continueTopic(data.topics, progress, data.problemCounts)
                    ?.first?.id
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), UiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    fun setUserName(name: String) {
        viewModelScope.launch { settingsRepository.setUserName(name.take(MAX_NAME)) }
    }

    private fun load() {
        viewModelScope.launch {
            content.value = UiState.Loading
            content.value = loadState {
                val topics = contentRepository.getTopics().takeIf { it.isNotEmpty() }
                topics?.let { ProfileContent(it, contentRepository.problemCounts(it)) }
            }
        }
    }

    private companion object {
        const val MAX_NAME = 40
    }
}
