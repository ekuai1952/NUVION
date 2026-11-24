package kz.nuvion.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LessonProgressEntity::class,
        QuizResultEntity::class,
        PracticeProgressEntity::class,
        UnlockedMissionEntity::class,
        UserStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NuvionDatabase : RoomDatabase() {

    abstract fun progressDao(): ProgressDao

    companion object {
        private const val NAME = "nuvion.db"

        fun create(context: Context): NuvionDatabase =
            Room.databaseBuilder(context.applicationContext, NuvionDatabase::class.java, NAME)
                // Demo app: a schema change wipes local progress instead of crashing.
                .fallbackToDestructiveMigration()
                .build()
    }
}
