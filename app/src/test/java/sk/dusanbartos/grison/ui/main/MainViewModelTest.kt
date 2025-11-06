package sk.dusanbartos.grison.ui.main

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val cardsRepository = mockk<CardsRepository>()
    private val logger = mockk<Logger>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val cardsFlow = MutableSharedFlow<Card>()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { cardsRepository.streamCard() } returns cardsFlow
        viewModel = MainViewModel(cardsRepository, logger, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - should start in loading state`() {
        // Then
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `init - when card received should update state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)

        // When
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(card, viewModel.state.value.card)
    }

    @Test
    fun `onEvent - GoToActivation should update state`() {
        // When
        viewModel.onEvent(MainScreenEvent.GoToActivation)

        // Then
        assertNotNull(viewModel.state.value.openActivation)
    }

    @Test
    fun `onEvent - GoToScratch should update state`() {
        // When
        viewModel.onEvent(MainScreenEvent.GoToScratch)

        // Then
        assertNotNull(viewModel.state.value.openScratch)
    }

    @Test
    fun `onEvent - Reset with no card should be a no-op`() {
        // When
        viewModel.onEvent(MainScreenEvent.Reset)

        // Then
        coVerify(exactly = 0) { cardsRepository.saveCard(any()) }
    }

    @Test
    fun `onEvent - Reset with card should save new state`() = runTest {
        // Given
        val card = Card(
            uuid = "123",
            state = CardState.Scratched,
            activationCode = "000000",
            scratchedAt = mockk(),
            activatedAt = mockk(),
        )
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { cardsRepository.saveCard(any()) } returns Result.success(Unit)

        // When
        viewModel.onEvent(MainScreenEvent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            cardsRepository.saveCard(match {
                it.uuid == card.uuid &&
                it.state == CardState.New &&
                it.activationCode == null &&
                it.scratchedAt == null &&
                it.activatedAt == null
            })
        }
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onEvent - Reset error should update loading state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { cardsRepository.saveCard(any()) } returns Result.failure(RuntimeException())

        // When
        viewModel.onEvent(MainScreenEvent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
    }
}