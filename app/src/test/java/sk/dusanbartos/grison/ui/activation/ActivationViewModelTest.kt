package sk.dusanbartos.grison.ui.activation

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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.activation.ActivateCardUseCase
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class ActivationViewModelTest {
    private val cardsRepository = mockk<CardsRepository>()
    private val activateCardUseCase = mockk<ActivateCardUseCase>()
    private val logger = mockk<Logger>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val cardsFlow = MutableSharedFlow<Card>()

    private lateinit var viewModel: ActivationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { cardsRepository.streamCard() } returns cardsFlow
        viewModel = ActivationViewModel(
            cardsRepository = cardsRepository,
            ioDispatcher = testDispatcher,
            activateCardUseCase = activateCardUseCase,
            logger = logger
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - should start in loading state`() {
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `init - when card received should update state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.Scratched)

        // When
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(card, viewModel.state.value.card)
    }

    @Test
    fun `activate - without card should be a no-op`() {
        // When
        viewModel.onEvent(ActivationScreenEvent.Activate)

        // Then
        coVerify(exactly = 0) { activateCardUseCase.invoke(any(), any()) }
    }

    @Test
    fun `activate - without activation code should be a no-op`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(ActivationScreenEvent.Activate)

        // Then
        coVerify(exactly = 0) { activateCardUseCase.invoke(any(), any()) }
    }

    @Test
    fun `activate - success should update state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.Scratched, activationCode = "000000")
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { activateCardUseCase.invoke(card, "000000") } returns Result.success(Unit)

        // When
        viewModel.onEvent(ActivationScreenEvent.Activate)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isActivating)
        assertNull(viewModel.state.value.activationError)
        coVerify { activateCardUseCase.invoke(card, "000000") }
    }

    @Test
    fun `activate - failure should update error state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.Scratched, activationCode = "000000")
        val error = RuntimeException("Activation failed")
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { activateCardUseCase.invoke(card, "000000") } returns Result.failure(error)

        // When
        viewModel.onEvent(ActivationScreenEvent.Activate)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isActivating)
        assertEquals(error, viewModel.state.value.activationError)
    }

    @Test
    fun `activationErrorProcessed - should clear error state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.Scratched, activationCode = "000000")
        val error = RuntimeException("Activation failed")
        cardsFlow.emit(card)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { activateCardUseCase.invoke(card, "000000") } returns Result.failure(error)

        // When
        viewModel.onEvent(ActivationScreenEvent.Activate)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onEvent(ActivationScreenEvent.ActivationErrorProcessed)

        // Then
        assertNull(viewModel.state.value.activationError)
    }
}