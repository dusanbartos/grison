package sk.dusanbartos.grison.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.dusanbartos.grison.storage.cards.CardEntity
import sk.dusanbartos.grison.storage.cards.CardsDao

@Database(
    entities = [
        CardEntity::class
    ],
    version = 1
)
@TypeConverters(RoomConverters::class)
abstract class GrisonDatabase : RoomDatabase() {
    abstract fun cardsDao(): CardsDao
}