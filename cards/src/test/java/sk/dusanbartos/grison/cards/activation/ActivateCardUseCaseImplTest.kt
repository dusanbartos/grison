package sk.dusanbartos.grison.cards.activation

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.activation.ActivationService
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import kotlin.time.Duration.Companion.seconds

class ActivateCardUseCaseImplTest {
    private val activationService = mockk<ActivationService>()
    private val cardsRepository = mockk<CardsRepository>()
    private val logger = mockk<Logger>(relaxed = true)

    private lateinit var useCase: ActivateCardUseCaseImpl

    @Before
    fun setup() {
        useCase = ActivateCardUseCaseImpl(
            activationService = activationService,
            cardsRepository = cardsRepository,
            logger = logger,
        )
    }

    @Test
    fun `activation success - should update card state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        val activationCode = "000000"
        coEvery { activationService.activateCard(activationCode) } returns Result.success("277029")
        coEvery { cardsRepository.saveCard(any()) } returns Result.success(Unit)

        // When
        val result = useCase.invoke(card, activationCode)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            cardsRepository.saveCard(match { savedCard ->
                savedCard.uuid == card.uuid &&
                savedCard.state == CardState.Activated &&
                savedCard.activatedAt != null
            })
        }
    }

    @Test
    fun `activation service failure - should return error`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        val error = RuntimeException("Network error")
        coEvery { activationService.activateCard(any()) } returns Result.failure(error)

        // When
        val result = useCase.invoke(card, "000000")

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 0) { cardsRepository.saveCard(any()) }
    }

    @Test
    fun `invalid result code - should return error`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        coEvery { activationService.activateCard(any()) } returns Result.success("277028")

        // When
        val result = useCase.invoke(card, "000000")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        coVerify(exactly = 0) { cardsRepository.saveCard(any()) }
    }

    @Test
    fun `non-numeric result code - should return error`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        coEvery { activationService.activateCard(any()) } returns Result.success("invalid")

        // When
        val result = useCase.invoke(card, "000000")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { cardsRepository.saveCard(any()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `cancellation should not stop the execution`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        coEvery { activationService.activateCard(any()) } coAnswers {
            delay(2.seconds)
            Result.success("277029")
        }
        coEvery { cardsRepository.saveCard(any()) } returns Result.success(Unit)

        // When
        val job = launch { useCase.invoke(card, "000000") }
        advanceTimeBy(1.seconds)
        // cancel before service delay passes
        job.cancel()
        advanceTimeBy(2.seconds)

        // Then
        coVerify(exactly = 1) {
            cardsRepository.saveCard(match { savedCard ->
                savedCard.uuid == card.uuid &&
                        savedCard.state == CardState.Activated &&
                        savedCard.activatedAt != null
            })
        }
    }
}