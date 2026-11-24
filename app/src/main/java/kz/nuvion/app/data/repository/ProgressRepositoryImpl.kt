package kz.nuvion.app.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kz.nuvion.app.data.local.database.LessonProgressEntity
import kz.nuvion.app.data.local.database.NuvionDatabase
import kz.nuvion.app.data.local.database.PracticeProgressEntity
import kz.nuvion.app.data.local.database.QuizResultEntity
import kz.nuvion.app.data.local.database.UnlockedMissionEntity
import kz.nuvion.app.data.local.database.UserStateEntity
import kz.nuvion.app.domain.model.QuizResult
import kz.nuvion.app.domain.model.UserProgress
import kz.nuvion.app.domain.repository.ContentRepository
import kz.nuvion.app.domain.repository.ProgressRepository
import kz.nuvion.app.domain.repository.QuizSaveOutcome
import kotlin.math.ceil

class ProgressRepositoryImpl(
    private val database: NuvionDatabase,
    private val contentRepository: ContentRepository,
    private val clock: () -> Long = System::currentTimeMillis
) : ProgressRepository {

    private val dao = database.progressDao()

    override val progress: Flow<UserProgress> = combine(
        dao.observeLessons(),
        dao.observeQuizResults(),
        dao.observePractice(),
        dao.observeUnlockedMissions(),
        dao.observeUserState()
    ) { lessons, quizzes, practice, missions, state ->
        UserProgress(
            totalXp = state?.totalXp ?: 0,
            completedLessons = lessons.mapTo(mutableSetOf()) { it.topicId },
            quizResults = quizzes.associate { entity ->
                entity.topicId to QuizResult(
                    topicId = entity.topicId,
                    lastScore = entity.lastScore,
                    bestScore = entity.bestScore,
                    total = entity.total,
                    passed = entity.passed,
                    attempts = entity.attempts
                )
            },
            solvedProblems = practice.mapTo(mutableSetOf()) {
                UserProgress.problemKey(it.topicId, it.problemId)
            },
            unlockedMissions = missions.mapTo(mutableSetOf()) { it.topicId },
            currentTopicId = state?.currentTopicId,
            lastRoute = state?.lastRoute,
            lastRewardXp = state?.lastRewardXp ?: 0
        )
    }

    override suspend fun completeLesson(topicId: String, xpReward: Int): Int {
        val awarded = database.withTransaction {
            val inserted = dao.insertLesson(LessonProgressEntity(topicId, clock())) != -1L
            val xp = if (inserted) xpReward else 0
            updateState { it.copy(totalXp = it.totalXp + xp, lastRewardXp = xp, currentTopicId = topicId) }
            xp
        }
        unlockNextIfComplete(topicId)
        return awarded
    }

    override suspend fun saveQuizResult(
        topicId: String,
        score: Int,
        total: Int,
        passThreshold: Float,
        xpReward: Int
    ): QuizSaveOutcome {
        val requiredScore = ceil(total * passThreshold).toInt()
        val passedNow = score >= requiredScore
        val outcome = database.withTransaction {
            val old = dao.getQuizResult(topicId)
            val giveXp = passedNow && old?.xpAwarded != true
            dao.upsertQuizResult(
                QuizResultEntity(
                    topicId = topicId,
                    lastScore = score,
                    bestScore = maxOf(score, old?.bestScore ?: 0),
                    total = total,
                    passed = passedNow || old?.passed == true,
                    xpAwarded = giveXp || old?.xpAwarded == true,
                    attempts = (old?.attempts ?: 0) + 1,
                    updatedAt = clock()
                )
            )
            val xp = if (giveXp) xpReward else 0
            updateState { it.copy(totalXp = it.totalXp + xp, lastRewardXp = xp, currentTopicId = topicId) }
            QuizSaveOutcome(xpAwarded = xp, passed = passedNow)
        }
        unlockNextIfComplete(topicId)
        return outcome
    }

    override suspend fun markProblemSolved(topicId: String, problemId: String, xpReward: Int): Int {
        val awarded = database.withTransaction {
            val inserted = dao.insertPractice(PracticeProgressEntity(topicId, problemId, clock())) != -1L
            val xp = if (inserted) xpReward else 0
            if (xp > 0) updateState { it.copy(totalXp = it.totalXp + xp, currentTopicId = topicId) }
            xp
        }
        unlockNextIfComplete(topicId)
        return awarded
    }

    override suspend fun setLastReward(xp: Int) = updateState { it.copy(lastRewardXp = xp) }

    override suspend fun setCurrentTopic(topicId: String) = updateState { it.copy(currentTopicId = topicId) }

    override suspend fun setLastRoute(route: String) = updateState { it.copy(lastRoute = route) }

    override suspend fun resetProgress() {
        database.withTransaction {
            dao.clearLessons()
            dao.clearQuizResults()
            dao.clearPractice()
            dao.clearMissions()
            dao.clearUserState()
        }
    }

    private suspend fun updateState(transform: (UserStateEntity) -> UserStateEntity) {
        database.withTransaction {
            val current = dao.getUserState() ?: UserStateEntity()
            dao.upsertUserState(transform(current))
        }
    }

    /** Stores the unlock of the next mission once the whole topic is complete. */
    private suspend fun unlockNextIfComplete(topicId: String) {
        val topics = contentRepository.getTopics().sortedBy { it.order }
        val index = topics.indexOfFirst { it.id == topicId }
        if (index < 0) return
        val totalProblems = contentRepository.getPracticeProblems(topicId).size
        val lessonDone = dao.isLessonCompleted(topicId)
        val quizPassed = dao.getQuizResult(topicId)?.passed == true
        val practiceDone = dao.solvedCount(topicId) >= totalProblems
        if (lessonDone && quizPassed && practiceDone) {
            topics.getOrNull(index + 1)?.let { next ->
                dao.insertUnlockedMission(UnlockedMissionEntity(next.id, clock()))
                updateState { it.copy(currentTopicId = next.id) }
            }
        }
    }
}
