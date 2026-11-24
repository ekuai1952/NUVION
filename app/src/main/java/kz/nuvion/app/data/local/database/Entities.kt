package kz.nuvion.app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val topicId: String,
    val completedAt: Long
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey val topicId: String,
    val lastScore: Int,
    val bestScore: Int,
    val total: Int,
    val passed: Boolean,
    /** XP for a quiz is given once, on the first passing attempt. */
    val xpAwarded: Boolean,
    val attempts: Int,
    val updatedAt: Long
)

@Entity(tableName = "practice_progress", primaryKeys = ["topicId", "problemId"])
data class PracticeProgressEntity(
    val topicId: String,
    val problemId: String,
    val solvedAt: Long
)

@Entity(tableName = "unlocked_missions")
data class UnlockedMissionEntity(
    @PrimaryKey val topicId: String,
    val unlockedAt: Long
)

/** Single-row table (id = 0) with totals and navigation state. */
@Entity(tableName = "user_state")
data class UserStateEntity(
    @PrimaryKey val id: Int = SINGLE_ROW_ID,
    val totalXp: Int = 0,
    val currentTopicId: String? = null,
    val lastRoute: String? = null,
    val lastRewardXp: Int = 0
) {
    companion object {
        const val SINGLE_ROW_ID = 0
    }
}
