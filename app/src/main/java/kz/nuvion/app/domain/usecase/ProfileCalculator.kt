package kz.nuvion.app.domain.usecase

import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.UserProgress
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

enum class Achievement {
    /** Completed the first lesson. */
    ATOM_EXPLORER,
    /** Passed the first quiz. */
    QUEST_MASTER,
    /** Solved 5 practice problems. */
    PROBLEM_SOLVER,
    /** Answered every question of a quiz correctly. */
    PERFECT_SCORE,
    /** Completed a whole topic (mission). */
    MISSION_PILOT,
    /** Completed all topics. */
    NUCLEAR_MASTER
}

data class AchievementState(val achievement: Achievement, val unlocked: Boolean)

data class ProfileStats(
    val totalXp: Int,
    val level: Int,
    /** XP collected inside the current level (0 until [xpPerLevel]). */
    val xpInLevel: Int,
    val xpPerLevel: Int,
    /** XP earned on each day of the current week, Monday first (7 values). */
    val weekXp: List<Int>,
    val weekStart: LocalDate,
    val todayIndex: Int,
    val streakDays: Int,
    /** Share of correct quiz answers (best attempts), or null if no quiz was taken. */
    val accuracyPercent: Int?,
    val topicsCompleted: Int,
    val topicsTotal: Int,
    val achievements: List<AchievementState>
) {
    val xpToNextLevel: Int get() = xpPerLevel - xpInLevel
    val levelFraction: Float get() = xpInLevel.toFloat() / xpPerLevel
}

object ProfileCalculator {

    /** 200 XP per level: the 5 demo topics (1200 XP) take a learner to level 7. */
    const val XP_PER_LEVEL = 200
    private const val PROBLEMS_FOR_SOLVER = 5

    fun stats(
        progress: UserProgress,
        topics: List<Topic>,
        problemCounts: Map<String, Int>,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault()
    ): ProfileStats {
        val xp = progress.totalXp.coerceAtLeast(0)

        val xpByDate: Map<LocalDate, Int> = progress.xpEvents
            .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }
            .mapValues { (_, events) -> events.sumOf { it.amount } }

        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weekXp = (0L until 7L).map { xpByDate[weekStart.plusDays(it)] ?: 0 }

        val completedTopics = topics.count { topic ->
            progress.topicProgress(topic.id, problemCounts[topic.id] ?: 0).isComplete
        }

        val quizzes = progress.quizResults.values
        val answered = quizzes.sumOf { it.total }
        val accuracy = if (answered == 0) null else quizzes.sumOf { it.bestScore } * 100 / answered

        val unlocked = mapOf(
            Achievement.ATOM_EXPLORER to progress.completedLessons.isNotEmpty(),
            Achievement.QUEST_MASTER to quizzes.any { it.passed },
            Achievement.PROBLEM_SOLVER to (progress.solvedProblems.size >= PROBLEMS_FOR_SOLVER),
            Achievement.PERFECT_SCORE to quizzes.any { it.total > 0 && it.bestScore == it.total },
            Achievement.MISSION_PILOT to (completedTopics >= 1),
            Achievement.NUCLEAR_MASTER to (topics.isNotEmpty() && completedTopics == topics.size)
        )

        return ProfileStats(
            totalXp = xp,
            level = xp / XP_PER_LEVEL + 1,
            xpInLevel = xp % XP_PER_LEVEL,
            xpPerLevel = XP_PER_LEVEL,
            weekXp = weekXp,
            weekStart = weekStart,
            todayIndex = (today.dayOfWeek.value - 1),
            streakDays = streak(xpByDate.keys, today),
            accuracyPercent = accuracy,
            topicsCompleted = completedTopics,
            topicsTotal = topics.size,
            achievements = Achievement.entries.map { AchievementState(it, unlocked[it] == true) }
        )
    }

    /**
     * Consecutive days with XP, counted back from today. A streak is still alive if the learner
     * studied yesterday but not yet today.
     */
    fun streak(activeDays: Set<LocalDate>, today: LocalDate): Int {
        var day = if (today in activeDays) today else today.minusDays(1)
        var count = 0
        while (day in activeDays) {
            count++
            day = day.minusDays(1)
        }
        return count
    }
}
