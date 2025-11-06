package sk.dusanbartos.grison.storage.cards

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.logger.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class RoomCardsStoreTest {
    private val cardsDao = mockk<CardsDao>()
    private val logger = mockk<Logger>(relaxed = true)
    private lateinit var store: RoomCardsStore

    @Before
    fun setup() {
        store = RoomCardsStore(cardsDao, logger)
    }

    @Test
    fun `fetch - success should map entities to cards`() = runTest {
        // Given
        val entity = CardEntity(
            id = "123",
            state = CardState.New,
            activationCode = null,
            scratchedAt = null,
            activatedAt = null
        )
        coEvery { cardsDao.getAll() } returns listOf(entity)

        // When
        val result = store.fetch()

        // Then
        assertTrue(result.isSuccess)
        val cards = result.getOrNull()
        requireNotNull(cards)
        assertEquals(1, cards.size)
        assertEquals(entity.id, cards[0].uuid)
        assertEquals(entity.state, cards[0].state)
    }

    @Test
    fun `fetch - dao error should return failure`() = runTest {
        // Given
        val error = RuntimeException("DB error")
        coEvery { cardsDao.getAll() } throws error

        // When
        val result = store.fetch()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `save - success should map card to entity`() = runTest {
        // Given
        val card = Card(
            uuid = "123",
            state = CardState.New,
            activationCode = null,
            scratchedAt = null,
            activatedAt = null
        )
        coEvery { cardsDao.upsert(any()) } returns Unit

        // When
        val result = store.save(card)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            cardsDao.upsert(match {
                it.id == card.uuid &&
                it.state == card.state &&
                it.activationCode == card.activationCode &&
                it.scratchedAt == card.scratchedAt &&
                it.activatedAt == card.activatedAt
            })
        }
    }

    @Test
    fun `save - dao error should return failure`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        val error = RuntimeException("DB error")
        coEvery { cardsDao.upsert(any()) } throws error

        // When
        val result = store.save(card)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}