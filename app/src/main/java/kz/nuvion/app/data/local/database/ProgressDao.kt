package kz.nuvion.app.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    // Lessons
    @Query("SELECT * FROM lesson_progress")
    fun observeLessons(): Flow<List<LessonProgressEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM lesson_progress WHERE topicId = :topicId)")
    suspend fun isLessonCompleted(topicId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLesson(entity: LessonProgressEntity): Long

    // Quizzes
    @Query("SELECT * FROM quiz_results")
    fun observeQuizResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE topicId = :topicId")
    suspend fun getQuizResult(topicId: String): QuizResultEntity?

    @Upsert
    suspend fun upsertQuizResult(entity: QuizResultEntity)

    // Practice
    @Query("SELECT * FROM practice_progress")
    fun observePractice(): Flow<List<PracticeProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPractice(entity: PracticeProgressEntity): Long

    @Query("SELECT COUNT(*) FROM practice_progress WHERE topicId = :topicId")
    suspend fun solvedCount(topicId: String): Int

    // Missions
    @Query("SELECT * FROM unlocked_missions")
    fun observeUnlockedMissions(): Flow<List<UnlockedMissionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnlockedMission(entity: UnlockedMissionEntity)

    // User state
    @Query("SELECT * FROM user_state WHERE id = 0")
    fun observeUserState(): Flow<UserStateEntity?>

    @Query("SELECT * FROM user_state WHERE id = 0")
    suspend fun getUserState(): UserStateEntity?

    @Upsert
    suspend fun upsertUserState(entity: UserStateEntity)

    // XP history
    @Query("SELECT * FROM xp_events ORDER BY timestamp")
    fun observeXpEvents(): Flow<List<XpEventEntity>>

    @Insert
    suspend fun insertXpEvent(entity: XpEventEntity)

    @Query("DELETE FROM xp_events")
    suspend fun clearXpEvents()

    // Reset
    @Query("DELETE FROM lesson_progress")
    suspend fun clearLessons()

    @Query("DELETE FROM quiz_results")
    suspend fun clearQuizResults()

    @Query("DELETE FROM practice_progress")
    suspend fun clearPractice()

    @Query("DELETE FROM unlocked_missions")
    suspend fun clearMissions()

    @Query("DELETE FROM user_state")
    suspend fun clearUserState()
}
