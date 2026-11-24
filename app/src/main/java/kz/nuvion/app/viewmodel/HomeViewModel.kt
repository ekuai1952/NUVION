package kz.nuvion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.repository.SettingsRepository
import kz.nuvion.app.domain.usecase.MissionCalculator
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState

data class HomeUiState(
    val isLoading: Boolean = true,
    val language: AppLanguage = AppLanguage.Default,
    val totalXp: Int = 0,
    val hasProgress: Boolean = false,
    /** Lesson that "Continue" opens: the current unfinished topic, or the first one. */
    val continueTopicId: String? = null,
    val showWelcome: Boolean = false,
    val hapticsEnabled: Boolean = true
)

private data class HomeContent(val topics: List<Topic>, val problemCounts: Map<String, Int>)

class HomeViewModel(
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val content = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)

    val uiState: StateFlow<HomeUiState> = combine(
        content,
        progressRepository.safeProgress(),
        settingsRepository.settings
    ) { contentState, progress, settings ->
        val data = (contentState as? UiState.Success)?.data
        val continueTopic = data?.let {
            MissionCalculator.continueTopic(it.topics, progress, it.problemCounts)?.first
        }
        HomeUiState(
            isLoading = contentState is UiState.Loading,
            language = settings.language,
            totalXp = progress.totalXp,
            hasProgress = progress.totalXp > 0 || progress.completedLessons.isNotEmpty(),
            continueTopicId = continueTopic?.id ?: data?.topics?.firstOrNull()?.id,
            showWelcome = settings.isFirstLaunch,
            hapticsEnabled = settings.hapticsEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState())

    init {
        viewModelScope.launch {
            content.value = loadState {
                val topics = contentRepository.getTopics()
                HomeContent(topics, contentRepository.problemCounts(topics))
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepository.setLanguage(language) }
    }

    fun dismissWelcome() {
        viewModelScope.launch { settingsRepository.setFirstLaunchDone() }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHapticsEnabled(enabled) }
    }

    fun resetProgress() {
        viewModelScope.launch { progressRepository.resetProgress() }
    }
}
