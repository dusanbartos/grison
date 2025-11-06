package sk.dusanbartos.grison.ui.scratch

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.domain.scratch.ScratchCardUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchViewModelTest {
    private val cardsRepository = mockk<CardsRepository>()
    private val scratchCardUseCase = mockk<ScratchCardUseCase>()
    private val logger = mockk<Logger>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val cardsFlow = MutableSharedFlow<Card>()

    private lateinit var viewModel: ScratchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { cardsRepository.streamCard() } returns cardsFlow
        viewModel = ScratchViewModel(
            cardsRepository = cardsRepository,
            ioDispatcher = testDispatcher,
            scratchCardUseCase = scratchCardUseCase,
            logger = logger
        )
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
    fun `scratch - without card should be a no-op`() {
        // When
        viewModel.onEvent(ScratchScreenEvent.Scratch)

        // Then
        coVerify(exactly = 0) { scratchCardUseCase.invoke(any()) }
    }

    @Test
    fun `scratch - success should update state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { scratchCardUseCase.invoke(card) } returns Result.success(Unit)

        // When
        viewModel.onEvent(ScratchScreenEvent.Scratch)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isScratching)
        coVerify { scratchCardUseCase.invoke(card) }
    }

    @Test
    fun `scratch - failure should update state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        val error = RuntimeException("Scratch failed")
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { scratchCardUseCase.invoke(card) } throws error

        // When
        viewModel.onEvent(ScratchScreenEvent.Scratch)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isScratching)
    }

    @Test
    fun `goToActivation - should update state`() = runTest {
        // When
        viewModel.onEvent(ScratchScreenEvent.GoToActivation)

        // Then
        assertNotNull(viewModel.state.value.openActivation)
    }

    @Test
    fun `openActivationProcessed - should clear navigation state`() = runTest {
        // Given
        viewModel.onEvent(ScratchScreenEvent.GoToActivation)

        // When
        viewModel.onEvent(ScratchScreenEvent.OpenActivationProcessed)

        // Then
        assertNull(viewModel.state.value.openActivation)
    }
}