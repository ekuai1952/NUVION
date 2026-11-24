package kz.nuvion.app

import android.content.Context
import kz.nuvion.app.data.local.database.NuvionDatabase
import kz.nuvion.app.data.local.datastore.SettingsDataStore
import kz.nuvion.app.data.repository.ContentRepositoryImpl
import kz.nuvion.app.data.repository.ProgressRepositoryImpl
import kz.nuvion.app.data.source.LocalContentDataSource
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.repository.SettingsRepository
import kz.nuvion.app.util.ArAvailabilityChecker

/**
 * Manual dependency container. To load content from a server, replace
 * [LocalContentDataSource] with a remote data source here; nothing else changes.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val database: NuvionDatabase by lazy { NuvionDatabase.create(appContext) }

    val contentRepository: ContentRepository by lazy {
        ContentRepositoryImpl(LocalContentDataSource())
    }

    val progressRepository: ProgressRepository by lazy {
        ProgressRepositoryImpl(database, contentRepository)
    }

    val settingsRepository: SettingsRepository by lazy { SettingsDataStore(appContext) }

    val arAvailabilityChecker: ArAvailabilityChecker by lazy { ArAvailabilityChecker(appContext) }
}
