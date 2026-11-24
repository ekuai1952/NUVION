package kz.nuvion.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        LessonProgressEntity::class,
        QuizResultEntity::class,
        PracticeProgressEntity::class,
        UnlockedMissionEntity::class,
        UserStateEntity::class,
        XpEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NuvionDatabase : RoomDatabase() {

    abstract fun progressDao(): ProgressDao

    companion object {
        private const val NAME = "nuvion.db"

        /** v2 adds the XP history table; existing progress is kept. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `xp_events` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`amount` INTEGER NOT NULL, " +
                        "`timestamp` INTEGER NOT NULL)"
                )
                // Backfill history from v1 progress (demo rewards: lesson 50, quiz 100, problem 30).
                db.execSQL("INSERT INTO xp_events (amount, timestamp) SELECT 50, completedAt FROM lesson_progress")
                db.execSQL("INSERT INTO xp_events (amount, timestamp) SELECT 100, updatedAt FROM quiz_results WHERE xpAwarded = 1")
                db.execSQL("INSERT INTO xp_events (amount, timestamp) SELECT 30, solvedAt FROM practice_progress")
            }
        }

        fun create(context: Context): NuvionDatabase =
            Room.databaseBuilder(context.applicationContext, NuvionDatabase::class.java, NAME)
                .addMigrations(MIGRATION_1_2)
                // Safety net for unknown future schema changes in this demo app.
                .fallbackToDestructiveMigration()
                .build()
    }
}
