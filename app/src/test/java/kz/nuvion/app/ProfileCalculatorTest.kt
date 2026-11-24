package kz.nuvion.app

import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.data.demo.TopicIds
import kz.nuvion.app.domain.model.QuizResult
import kz.nuvion.app.domain.model.UserProgress
import kz.nuvion.app.domain.model.XpEvent
import kz.nuvion.app.domain.usecase.Achievement
import kz.nuvion.app.domain.usecase.ProfileCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class ProfileCalculatorTest {

    private val zone = ZoneOffset.UTC
    private val today = LocalDate.of(2026, 11, 26) // Thursday
    private val counts = DemoTopics.all.associate { it.id to 3 }

    private fun at(date: LocalDate) = date.atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000

    @Test
    fun emptyProgress_levelOne_noStats() {
        val s = ProfileCalculator.stats(UserProgress(), DemoTopics.all, counts, today, zone)
        assertEquals(1, s.level)
        assertEquals(200, s.xpToNextLevel)
        assertEquals(List(7) { 0 }, s.weekXp)
        assertEquals(0, s.streakDays)
        assertNull(s.accuracyPercent)
        assertTrue(s.achievements.none { it.unlocked })
        assertEquals(3, s.todayIndex)
    }

    @Test
    fun levelsAndWeeklyChart() {
        val progress = UserProgress(
            totalXp = 450,
            xpEvents = listOf(
                XpEvent(50, at(today.minusDays(3))), // Monday
                XpEvent(100, at(today)),
                XpEvent(30, at(today)),
                XpEvent(270, at(today.minusDays(10))) // previous week: not in chart
            )
        )
        val s = ProfileCalculator.stats(progress, DemoTopics.all, counts, today, zone)
        assertEquals(3, s.level)
        assertEquals(50, s.xpInLevel)
        assertEquals(listOf(50, 0, 0, 130, 0, 0, 0), s.weekXp)
        assertEquals(LocalDate.of(2026, 11, 23), s.weekStart)
    }

    @Test
    fun streakCountsBackFromTodayOrYesterday() {
        val days = setOf(today.minusDays(1), today.minusDays(2), today.minusDays(4))
        assertEquals(2, ProfileCalculator.streak(days, today))
        assertEquals(3, ProfileCalculator.streak(days + today, today))
        assertEquals(0, ProfileCalculator.streak(setOf(today.minusDays(3)), today))
    }

    @Test
    fun accuracyAndAchievements() {
        val id = TopicIds.ATOM_MODELS
        val progress = UserProgress(
            totalXp = 240,
            completedLessons = setOf(id),
            quizResults = mapOf(
                id to QuizResult(id, 5, 5, 5, passed = true, attempts = 1),
                TopicIds.BOHR to QuizResult(TopicIds.BOHR, 2, 2, 5, passed = false, attempts = 1)
            ),
            solvedProblems = setOf("$id/p1", "$id/p2", "$id/p3")
        )
        val s = ProfileCalculator.stats(progress, DemoTopics.all, counts, today, zone)
        assertEquals(70, s.accuracyPercent) // 7 of 10
        assertEquals(1, s.topicsCompleted)
        val unlocked = s.achievements.filter { it.unlocked }.map { it.achievement }.toSet()
        assertEquals(
            setOf(Achievement.ATOM_EXPLORER, Achievement.QUEST_MASTER, Achievement.PERFECT_SCORE, Achievement.MISSION_PILOT),
            unlocked
        )
        assertFalse(Achievement.NUCLEAR_MASTER in unlocked)
    }
}
