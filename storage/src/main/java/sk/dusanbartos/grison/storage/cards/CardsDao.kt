package sk.dusanbartos.grison.storage.cards

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CardsDao {
    @Query("SELECT * FROM cards")
    suspend fun getAll(): List<CardEntity>

    @Upsert
    suspend fun upsert(card: CardEntity)
}