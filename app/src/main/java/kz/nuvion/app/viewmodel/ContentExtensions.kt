package kz.nuvion.app.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.UserProgress
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository

/** Number of practice problems per topic id. */
internal suspend fun ContentRepository.problemCounts(topics: List<Topic>): Map<String, Int> =
    topics.associate { it.id to getPracticeProblems(it.id).size }

/** Progress stream that falls back to empty progress if the database cannot be read. */
internal fun ProgressRepository.safeProgress(): Flow<UserProgress> =
    progress.catch { emit(UserProgress()) }

internal const val STOP_TIMEOUT_MS = 5_000L
