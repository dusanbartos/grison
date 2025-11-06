package sk.dusanbartos.grison.domain.cards

import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    fun start()
    fun streamCard(): Flow<Card>
    suspend fun saveCard(card: Card): Result<Unit>
}