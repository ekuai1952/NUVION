package kz.nuvion.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/** "Haptic feedback" switch from settings (DataStore). */
val LocalHapticsEnabled = staticCompositionLocalOf { true }

/** Vibrates once each time [key] changes to a non-null value, if haptics are enabled. */
@Composable
fun HapticOnChange(key: Any?) {
    val haptics = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    LaunchedEffect(key) {
        if (key != null && enabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}
