package kz.nuvion.app.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kz.nuvion.app.AppContainer
import kz.nuvion.app.NuvionApplication

/** Creates every ViewModel with its dependencies; SavedStateHandle carries navigation arguments. */
object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { MainViewModel(container().settingsRepository, container().progressRepository) }
        initializer {
            HomeViewModel(
                container().contentRepository,
                container().progressRepository,
                container().settingsRepository
            )
        }
        initializer { TopicsViewModel(container().contentRepository, container().progressRepository) }
        initializer {
            LessonViewModel(createSavedStateHandle(), container().contentRepository, container().progressRepository)
        }
        initializer {
            QuizViewModel(createSavedStateHandle(), container().contentRepository, container().progressRepository)
        }
        initializer {
            PracticeViewModel(createSavedStateHandle(), container().contentRepository, container().progressRepository)
        }
        initializer { LibraryViewModel(container().contentRepository) }
        initializer { LibraryDetailViewModel(createSavedStateHandle(), container().contentRepository) }
        initializer { MissionMapViewModel(container().contentRepository, container().progressRepository) }
        initializer { ArLabViewModel(createSavedStateHandle(), container().arAvailabilityChecker) }
        initializer {
            ResultViewModel(createSavedStateHandle(), container().contentRepository, container().progressRepository)
        }
    }

    private fun CreationExtras.container(): AppContainer =
        (this[APPLICATION_KEY] as NuvionApplication).container
}
