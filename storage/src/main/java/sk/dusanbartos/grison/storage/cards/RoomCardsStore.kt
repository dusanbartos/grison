package sk.dusanbartos.grison.storage.cards

import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardsStore
import sk.dusanbartos.grison.domain.logger.Logger
import javax.inject.Inject

class RoomCardsStore @Inject constructor(
    private val cardsDao: CardsDao,
    private val logger: Logger
) : CardsStore {
    override suspend fun fetch(): Result<List<Card>> {
        try {
            val cards = cardsDao.getAll().map { it.toCard() }
            return Result.success(cards)
        } catch (e: Throwable) {
            logger.e(TAG, "fetch:error", e)
            return Result.failure(e)
        }
    }

    override suspend fun save(card: Card): Result<Unit> {
        try {
            cardsDao.upsert(card.toCardEntity())
            return Result.success(Unit)
        } catch (e: Throwable) {
            logger.e(TAG, "save:error", e)
            return Result.failure(e)
        }
    }

    private fun Card.toCardEntity(): CardEntity =
        CardEntity(
            id = uuid,
            state = state,
            activationCode = activationCode,
            scratchedAt = scratchedAt,
            activatedAt = activatedAt,
        )

    private fun CardEntity.toCard(): Card =
        Card(
            uuid = id,
            state = state,
            activationCode = activationCode,
            scratchedAt = scratchedAt,
            activatedAt = activatedAt,
        )

    companion object Companion {
        private val TAG = RoomCardsStore::class
    }
}