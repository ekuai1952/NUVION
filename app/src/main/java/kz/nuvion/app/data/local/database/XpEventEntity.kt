package kz.nuvion.app.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One XP award (lesson, quiz or problem). Used for the weekly chart and the streak. */
@Entity(tableName = "xp_events")
data class XpEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val timestamp: Long
)
