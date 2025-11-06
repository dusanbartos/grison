package sk.dusanbartos.grison.cards

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsStore
import sk.dusanbartos.grison.domain.logger.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class CardsRepositoryImplTest {
    private val cardsStore = mockk<CardsStore>()
    private val logger = mockk<Logger>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var repository: CardsRepositoryImpl

    @Before
    fun setup() {
        repository = CardsRepositoryImpl(
            logger = logger,
            cardsStore = cardsStore,
            ioDispatcher = testDispatcher,
            scope = testScope
        )
    }

    @Test
    fun `start - with existing card should initialize cache`() = runTest {
        // Given
        val existingCard = Card(uuid = "123", state = CardState.New)
        coEvery { cardsStore.fetch() } returns Result.success(listOf(existingCard))

        // When
        repository.start()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val emittedCard = repository.streamCard().first()
        assertEquals(existingCard, emittedCard)
    }

    @Test
    fun `start - with no cards should create new one`() = runTest {
        // Given
        coEvery { cardsStore.fetch() } returns Result.success(emptyList())
        coEvery { cardsStore.save(any()) } returns Result.success(Unit)

        // When
        repository.start()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val emittedCard = repository.streamCard().first()
        assertTrue(emittedCard.uuid.isNotEmpty())
        assertEquals(CardState.New, emittedCard.state)
        coVerify { cardsStore.save(match { it.state == CardState.New }) }
    }

    @Test
    fun `start - read failure should create new one`() = runTest {
        // Given
        coEvery { cardsStore.fetch() } returns Result.failure(RuntimeException("Read failure"))
        coEvery { cardsStore.save(any()) } returns Result.success(Unit)

        // When
        repository.start()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val emittedCard = repository.streamCard().first()
        assertTrue(emittedCard.uuid.isNotEmpty())
        assertEquals(CardState.New, emittedCard.state)
        coVerify { cardsStore.save(match { it.state == CardState.New }) }
    }

    @Test
    fun `saveCard - success should update cache`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.Scratched)
        coEvery { cardsStore.save(card) } returns Result.success(Unit)

        // When
        val result = repository.saveCard(card)

        // Then
        assertTrue(result.isSuccess)
        val emittedCard = repository.streamCard().first()
        assertEquals(card, emittedCard)
    }

    @Test
    fun `saveCard - failure should not update cache`() = runTest {
        // Given
        val initialCard = Card(uuid = "123", state = CardState.New)
        val updatedCard = initialCard.copy(state = CardState.Scratched)
        val error = RuntimeException("Store error")

        coEvery { cardsStore.fetch() } returns Result.success(listOf(initialCard))
        coEvery { cardsStore.save(updatedCard) } returns Result.failure(error)

        // Initialize cache
        repository.start()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = repository.saveCard(updatedCard)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        val emittedCard = repository.streamCard().first()
        assertEquals(initialCard, emittedCard)
    }

    @Test
    fun `streamCard - should not emit empty card`() = runTest {
        // Given
        val flow = repository.streamCard()
        val emittedCards = mutableListOf<Card>()

        // When
        val job = launch { flow.collect { emittedCards.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(emittedCards.isEmpty())
        job.cancel()
    }
}