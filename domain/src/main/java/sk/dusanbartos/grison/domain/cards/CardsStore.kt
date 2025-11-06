package sk.dusanbartos.grison.domain.cards

interface CardsStore {
    suspend fun fetch(): Result<List<Card>>
    suspend fun save(card: Card): Result<Unit>
}