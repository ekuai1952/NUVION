package kz.nuvion.app.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val NuvionLightColors = lightColorScheme(
    primary = NuvionColors.Blue,
    onPrimary = NuvionColors.Surface,
    primaryContainer = NuvionColors.BlueTint,
    onPrimaryContainer = NuvionColors.Navy,
    secondary = NuvionColors.Yellow,
    onSecondary = NuvionColors.Navy,
    secondaryContainer = NuvionColors.YellowTint,
    onSecondaryContainer = NuvionColors.Navy,
    tertiary = NuvionColors.Navy,
    onTertiary = NuvionColors.Surface,
    background = NuvionColors.Background,
    onBackground = NuvionColors.TextPrimary,
    surface = NuvionColors.Surface,
    onSurface = NuvionColors.TextPrimary,
    surfaceVariant = NuvionColors.BlueTint,
    onSurfaceVariant = NuvionColors.TextSecondary,
    outline = NuvionColors.Outline,
    error = NuvionColors.Error,
    onError = NuvionColors.Surface
)

/** NUVION is designed as a light-theme app, as in the mock-up. */
@Composable
fun NuvionTheme(content: @Composable () -> Unit) {
    val widthDp = LocalConfiguration.current.screenWidthDp
    val dimens = if (widthDp >= 600) ExpandedDimens else CompactDimens
    CompositionLocalProvider(LocalDimens provides dimens) {
        MaterialTheme(
            colorScheme = NuvionLightColors,
            typography = NuvionTypography,
            shapes = NuvionShapes,
            content = content
        )
    }
}

object NuvionThemeValues {
    val dimens: NuvionDimens
        @Composable @ReadOnlyComposable get() = LocalDimens.current
}

/**
 * Switches the status bar icon colour so it stays readable on blue/navy headers
 * (light icons) or white screens (dark icons).
 */
@Composable
fun SystemBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = darkIcons
        controller.isAppearanceLightNavigationBars = true
    }
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
