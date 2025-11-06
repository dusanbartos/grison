package sk.dusanbartos.grison.cards

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.dusanbartos.grison.domain.ApplicationScope
import sk.dusanbartos.grison.domain.IoDispatcher
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.cards.CardsStore
import sk.dusanbartos.grison.domain.logger.Logger
import javax.inject.Inject
import kotlin.uuid.Uuid

class CardsRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val cardsStore: CardsStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : CardsRepository {

    private val _card = MutableStateFlow(Card.EMPTY)

    override fun start() {
        scope.launch {
            try {
                val cards = cardsStore.fetch()
                // [ASS-1]
                // we currently only support a single card,
                // but the underlying CardStore is ready for multi-card support
                val card = cards.getOrNull()?.firstOrNull() ?: createNewCard()
                _card.value = card
                logger.d(TAG, "card cache initialized with $card")
            } catch (e: Throwable) {
                logger.e(TAG, "initialization failed", e)
            }
        }
    }

    override fun streamCard(): Flow<Card> = _card.filter { it != Card.EMPTY }

    override suspend fun saveCard(card: Card): Result<Unit> = withContext(ioDispatcher) {
        logger.d(TAG, "updateCard card=$card")
        cardsStore.save(card).also { result ->
            // update cache after success
            if (result.isSuccess) {
                _card.value = card
            }
        }
    }

    private suspend fun createNewCard(): Card = withContext(ioDispatcher) {
        logger.d(TAG, "createNewCard")
        val card = Card(
            uuid = Uuid.random().toHexDashString(),
            state = CardState.New,
        )
        cardsStore.save(card).exceptionOrNull()?.let { error ->
            // in case there is a DB error, delegate the error upstream
            throw error
        }
        return@withContext card
    }

    companion object {
        private val TAG = CardsRepositoryImpl::class
    }
}