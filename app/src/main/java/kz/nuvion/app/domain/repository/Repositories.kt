package kz.nuvion.app.domain.repository

import kotlinx.coroutines.flow.Flow
import kz.nuvion.app.domain.model.AppLanguage
import kz.nuvion.app.domain.model.LibraryMaterial
import kz.nuvion.app.domain.model.Lesson
import kz.nuvion.app.domain.model.PracticeProblem
import kz.nuvion.app.domain.model.Quiz
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.UserProgress

/**
 * Learning content. The current implementation reads bundled demo data; a REST or Firebase
 * data source can be plugged in behind the same interface.
 */
interface ContentRepository {
    suspend fun getTopics(): List<Topic>
    suspend fun getTopic(topicId: String): Topic?
    suspend fun getLesson(topicId: String): Lesson?
    suspend fun getQuiz(topicId: String): Quiz?
    suspend fun getPracticeProblems(topicId: String): List<PracticeProblem>
    suspend fun getLibraryMaterials(): List<LibraryMaterial>
    suspend fun getLibraryMaterial(materialId: String): LibraryMaterial?
}

data class QuizSaveOutcome(val xpAwarded: Int, val passed: Boolean)

/** Learner progress, persisted in Room. */
interface ProgressRepository {
    val progress: Flow<UserProgress>

    /** Returns XP awarded now (0 if the lesson was already completed). */
    suspend fun completeLesson(topicId: String, xpReward: Int): Int

    suspend fun saveQuizResult(
        topicId: String,
        score: Int,
        total: Int,
        passThreshold: Float,
        xpReward: Int
    ): QuizSaveOutcome

    /** Returns XP awarded now (0 if the problem was already solved). */
    suspend fun markProblemSolved(topicId: String, problemId: String, xpReward: Int): Int

    suspend fun setLastReward(xp: Int)
    suspend fun setCurrentTopic(topicId: String)
    suspend fun setLastRoute(route: String)
    suspend fun resetProgress()
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.Default,
    val isFirstLaunch: Boolean = true,
    val hapticsEnabled: Boolean = true,
    /** Learner's display name for the profile; empty until they set it. */
    val userName: String = ""
)

/** Language and app settings, persisted in DataStore. */
interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setFirstLaunchDone()
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setUserName(name: String)
}
