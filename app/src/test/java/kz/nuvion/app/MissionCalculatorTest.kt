package kz.nuvion.app

import kz.nuvion.app.data.demo.DemoTopics
import kz.nuvion.app.data.demo.TopicIds
import kz.nuvion.app.domain.model.MissionState
import kz.nuvion.app.domain.model.QuizResult
import kz.nuvion.app.domain.model.UserProgress
import kz.nuvion.app.domain.usecase.MissionCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class MissionCalculatorTest {

    private val topics = DemoTopics.all
    private val counts = topics.associate { it.id to 3 }

    @Test
    fun noProgress_firstIsCurrent_restLocked() {
        val missions = MissionCalculator.missions(topics, UserProgress(), counts)
        assertEquals(MissionState.CURRENT, missions[0].state)
        missions.drop(1).forEach { assertEquals(MissionState.LOCKED, it.state) }
    }

    @Test
    fun lessonOnly_doesNotUnlockNext() {
        val progress = UserProgress(completedLessons = setOf(TopicIds.ATOM_MODELS))
        val missions = MissionCalculator.missions(topics, progress, counts)
        assertEquals(MissionState.CURRENT, missions[0].state)
        assertEquals(MissionState.LOCKED, missions[1].state)
    }

    @Test
    fun completeTopic_unlocksNext() {
        val id = TopicIds.ATOM_MODELS
        val progress = UserProgress(
            completedLessons = setOf(id),
            quizResults = mapOf(id to QuizResult(id, 4, 4, 5, passed = true, attempts = 1)),
            solvedProblems = setOf("$id/p1", "$id/p2", "$id/p3")
        )
        val missions = MissionCalculator.missions(topics, progress, counts)
        assertEquals(MissionState.COMPLETED, missions[0].state)
        assertEquals(MissionState.CURRENT, missions[1].state)
        assertEquals(MissionState.LOCKED, missions[2].state)
        assertEquals(TopicIds.BOHR, MissionCalculator.continueTopic(topics, progress, counts)?.first?.id)
    }
}
