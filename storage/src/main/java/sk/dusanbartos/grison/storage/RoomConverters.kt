package sk.dusanbartos.grison.storage

import androidx.room.TypeConverter
import kotlin.time.Instant

class RoomConverters {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilliseconds()
}