package kz.nuvion.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.ArModel
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.util.ArAvailabilityChecker
import kz.nuvion.app.util.ArSupport

data class ArLabUiState(
    val arSupport: ArSupport = ArSupport.CHECKING,
    val selectedModel: ArModel = ArModel.BOHR_ATOM,
    val bundledGlbFiles: Set<String> = emptySet(),
    val autoRotate: Boolean = true
) {
    val selectedHasGlb: Boolean get() = selectedModel.glbFile in bundledGlbFiles
}

class ArLabViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val checker: ArAvailabilityChecker
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ArLabUiState(
            selectedModel = ArModel.fromId(
                savedStateHandle.get<String>(KEY_SELECTED) ?: savedStateHandle.get<String>(NavArgs.MODEL)
            ),
            autoRotate = savedStateHandle[KEY_AUTO_ROTATE] ?: true
        )
    )
    val uiState: StateFlow<ArLabUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val glb = checker.bundledModels()
            _uiState.update { it.copy(bundledGlbFiles = glb) }
            val support = checker.checkArSupport()
            _uiState.update { it.copy(arSupport = support) }
        }
    }

    fun selectModel(model: ArModel) {
        savedStateHandle[KEY_SELECTED] = model.id
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun setAutoRotate(enabled: Boolean) {
        savedStateHandle[KEY_AUTO_ROTATE] = enabled
        _uiState.update { it.copy(autoRotate = enabled) }
    }

    private companion object {
        const val KEY_SELECTED = "ar_selected_model"
        const val KEY_AUTO_ROTATE = "ar_auto_rotate"
    }
}
