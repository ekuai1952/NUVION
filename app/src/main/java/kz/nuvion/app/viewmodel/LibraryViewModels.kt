package kz.nuvion.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kz.nuvion.app.domain.model.LibraryMaterial
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.navigation.NavArgs
import kz.nuvion.app.util.UiState
import kz.nuvion.app.util.loadState

class LibraryViewModel(private val contentRepository: ContentRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<LibraryMaterial>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<LibraryMaterial>>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = loadState { contentRepository.getLibraryMaterials().takeIf { it.isNotEmpty() } }
        }
    }
}

class LibraryDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val materialId: String = savedStateHandle.get<String>(NavArgs.MATERIAL_ID).orEmpty()

    private val _uiState = MutableStateFlow<UiState<LibraryMaterial>>(UiState.Loading)
    val uiState: StateFlow<UiState<LibraryMaterial>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = loadState {
                contentRepository.getLibraryMaterial(materialId)?.takeIf { it.available }
            }
        }
    }
}
