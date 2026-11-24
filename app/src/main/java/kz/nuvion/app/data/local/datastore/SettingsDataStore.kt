package kz.nuvion.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.domain.repository.AppSettings
import kz.nuvion.app.domain.repository.SettingsRepository
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "nuvion_settings")

class SettingsDataStore(context: Context) : SettingsRepository {

    private val dataStore = context.applicationContext.settingsDataStore

    override val settings: Flow<AppSettings> = dataStore.data
        .catch { error ->
            // A corrupted or unreadable file must not crash the app: fall back to defaults.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            AppSettings(
                language = AppLanguage.fromTag(prefs[KEY_LANGUAGE]),
                isFirstLaunch = prefs[KEY_FIRST_LAUNCH] ?: true,
                hapticsEnabled = prefs[KEY_HAPTICS] ?: true,
                userName = prefs[KEY_USER_NAME].orEmpty()
            )
        }

    override suspend fun setUserName(name: String) {
        dataStore.edit { it[KEY_USER_NAME] = name.trim() }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[KEY_LANGUAGE] = language.tag }
    }

    override suspend fun setFirstLaunchDone() {
        dataStore.edit { it[KEY_FIRST_LAUNCH] = false }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    private companion object {
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
    }
}
