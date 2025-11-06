package sk.dusanbartos.grison.cards.scratch

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.domain.scratch.ScratchService

class ScratchCardUseCaseImplTest {
    private val scratchService = mockk<ScratchService>()
    private val cardsRepository = mockk<CardsRepository>()
    private val logger = mockk<Logger>(relaxed = true)

    private lateinit var useCase: ScratchCardUseCaseImpl

    @Before
    fun setup() {
        useCase = ScratchCardUseCaseImpl(scratchService, cardsRepository, logger)
    }

    @Test
    fun `scratch success - should update card state`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        coEvery { scratchService.scratchCard() } returns Result.success("000000")
        coEvery { cardsRepository.saveCard(any()) } returns Result.success(Unit)

        // When
        val result = useCase.invoke(card)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            cardsRepository.saveCard(match { savedCard ->
                savedCard.uuid == card.uuid &&
                savedCard.state == CardState.Scratched &&
                savedCard.activationCode == "000000" &&
                savedCard.scratchedAt != null
            })
        }
    }

    @Test
    fun `scratch service failure - should return error`() = runTest {
        // Given
        val card = Card(uuid = "123", state = CardState.New)
        val error = RuntimeException("Network error")
        coEvery { scratchService.scratchCard() } returns Result.failure(error)

        // When
        val result = useCase.invoke(card)

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify(exactly = 0) { cardsRepository.saveCard(any()) }
    }
}