package kz.nuvion.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.repository.SettingsRepository

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Ready(val language: AppLanguage, val hapticsEnabled: Boolean) : MainUiState
}

/** Activity-level state: waits for the saved language before showing the UI. */
class MainViewModel(
    settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = settingsRepository.settings
        .map<_, MainUiState> { MainUiState.Ready(it.language, it.hapticsEnabled) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState.Loading)

    /** Stores the last opened screen in Room. Failures are ignored: it is non-critical state. */
    fun onRouteChanged(route: String) {
        viewModelScope.launch {
            try {
                progressRepository.setLastRoute(route)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }
    }
}
