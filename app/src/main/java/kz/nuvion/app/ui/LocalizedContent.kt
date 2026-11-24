package kz.nuvion.app.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.domain.model.LocalizedText
import java.util.Locale

/** Current interface language, used to pick the right translation of learning content. */
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.Default }

/**
 * Applies [language] to everything below it without recreating the Activity: stringResource()
 * reads LocalContext/LocalConfiguration, so switching KK/RU/EN updates the UI immediately.
 * Kazakh has no res/values-kk folder on purpose: res/values is the Kazakh default.
 */
@Composable
fun LocalizedContent(language: AppLanguage, content: @Composable () -> Unit) {
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val localized = remember(language, baseConfiguration, baseContext) {
        val locale = Locale.forLanguageTag(language.tag)
        val configuration = Configuration(baseConfiguration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        val resources = baseContext.createConfigurationContext(configuration).resources
        LocalizedContextWrapper(baseContext, resources) to configuration
    }
    CompositionLocalProvider(
        LocalContext provides localized.first,
        LocalConfiguration provides localized.second,
        LocalAppLanguage provides language,
        content = content
    )
}

/**
 * Dialogs and popups are composed in their own window, which resets LocalContext and
 * LocalConfiguration to the system locale. Wrap dialog content in this to keep the app language.
 */
@Composable
fun Localized(content: @Composable () -> Unit) {
    LocalizedContent(language = LocalAppLanguage.current, content = content)
}

/** Keeps the Activity as base context (needed by system bars, dialogs) but swaps resources. */
private class LocalizedContextWrapper(base: Context, private val localizedResources: Resources) :
    ContextWrapper(base) {
    override fun getResources(): Resources = localizedResources
}

/** Picks the translation for the current interface language. */
@Composable
@ReadOnlyComposable
fun LocalizedText.text(): String = resolve(LocalAppLanguage.current)
