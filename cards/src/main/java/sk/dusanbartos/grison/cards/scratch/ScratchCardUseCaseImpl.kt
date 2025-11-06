package sk.dusanbartos.grison.cards.scratch

import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.domain.scratch.ScratchCardUseCase
import sk.dusanbartos.grison.domain.scratch.ScratchService
import javax.inject.Inject
import kotlin.time.Clock

class ScratchCardUseCaseImpl @Inject constructor(
    private val scratchService: ScratchService,
    private val cardsRepository: CardsRepository,
    private val logger: Logger,
) : ScratchCardUseCase {

    override suspend fun invoke(card: Card): Result<Unit> {
        val result = scratchService.scratchCard()

        val code = result.getOrNull()
        if (code == null) {
            logger.d(TAG, "scratch:failure result=$result")
            val error = result.exceptionOrNull() ?: Exception("Unknown failure")
            return Result.failure(error)
        }

        logger.d(TAG, "scratch:success code=$code")
        cardsRepository.saveCard(
            card.copy(
                state = CardState.Scratched,
                activationCode = code,
                scratchedAt = Clock.System.now()
            )
        )
        return Result.success(Unit)
    }

    companion object {
        private val TAG = ScratchCardUseCaseImpl::class
    }
}