package kz.nuvion.app.domain.usecase

import kz.nuvion.app.domain.model.Mission
import kz.nuvion.app.domain.model.MissionState
import kz.nuvion.app.domain.model.Topic
import kz.nuvion.app.domain.model.TopicProgress
import kz.nuvion.app.domain.model.UserProgress

/**
 * Builds the mission map. Mission N+1 unlocks once topic N is complete
 * (lesson + passed quiz + all practice problems).
 */
object MissionCalculator {

    fun missions(
        topics: List<Topic>,
        progress: UserProgress,
        problemCounts: Map<String, Int>
    ): List<Mission> {
        val sorted = topics.sortedBy { it.order }
        var previousComplete = true
        var currentAssigned = false
        return sorted.map { topic ->
            val topicProgress = progress.topicProgress(topic.id, problemCounts[topic.id] ?: 0)
            val unlocked = previousComplete || topic.id in progress.unlockedMissions
            val state = when {
                topicProgress.isComplete -> MissionState.COMPLETED
                unlocked && !currentAssigned -> MissionState.CURRENT.also { currentAssigned = true }
                else -> MissionState.LOCKED
            }
            previousComplete = topicProgress.isComplete
            Mission(topic.id, topic.order, topic.title, state, topicProgress)
        }
    }

    /** The topic "Continue" should open: the current mission, or the first topic. */
    fun continueTopic(
        topics: List<Topic>,
        progress: UserProgress,
        problemCounts: Map<String, Int>
    ): Pair<Topic, TopicProgress>? {
        val sorted = topics.sortedBy { it.order }
        if (sorted.isEmpty()) return null
        val missions = missions(sorted, progress, problemCounts)
        val preferred = progress.currentTopicId
            ?.let { id -> missions.firstOrNull { it.topicId == id && it.state == MissionState.CURRENT } }
        val target = preferred
            ?: missions.firstOrNull { it.state == MissionState.CURRENT }
            ?: missions.first()
        val topic = sorted.first { it.id == target.topicId }
        return topic to target.progress
    }
}
