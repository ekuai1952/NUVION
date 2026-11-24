package kz.nuvion.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.nuvion.app.navigation.NuvionNavHost
import kz.nuvion.app.ui.components.LoadingState
import kz.nuvion.app.ui.theme.NuvionColors
import kz.nuvion.app.ui.theme.NuvionTheme
import kz.nuvion.app.viewmodel.AppViewModelProvider
import kz.nuvion.app.viewmodel.MainUiState
import kz.nuvion.app.viewmodel.MainViewModel

/** Root composable: theme + saved language + navigation. */
@Composable
fun NuvionAppRoot(
    mainViewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    NuvionTheme {
        when (val s = state) {
            MainUiState.Loading -> LoadingState(Modifier.background(NuvionColors.Background))
            is MainUiState.Ready -> LocalizedContent(language = s.language) {
                CompositionLocalProvider(LocalHapticsEnabled provides s.hapticsEnabled) {
                    NuvionNavHost(
                        onRouteChanged = mainViewModel::onRouteChanged,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NuvionColors.Background)
                    )
                }
            }
        }
    }
}
