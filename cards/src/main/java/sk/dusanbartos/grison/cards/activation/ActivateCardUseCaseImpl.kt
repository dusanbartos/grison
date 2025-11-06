package sk.dusanbartos.grison.cards.activation

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import sk.dusanbartos.grison.domain.activation.ActivateCardUseCase
import sk.dusanbartos.grison.domain.activation.ActivationService
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import javax.inject.Inject
import kotlin.time.Clock

class ActivateCardUseCaseImpl @Inject constructor(
    private val activationService: ActivationService,
    private val cardsRepository: CardsRepository,
    private val logger: Logger,
) : ActivateCardUseCase {

    override suspend fun invoke(
        card: Card,
        activationCode: String,
    ): Result<Unit> =
        // activation is a non-cancellable operation
        // even if the caller scope is cancelled, this will make sure the operation completes
        withContext(NonCancellable) {
            val result = activationService.activateCard(activationCode)
            val resultCode = result.getOrNull()
            if (resultCode == null) {
                val error = result.exceptionOrNull() ?: Exception("Unknown failure")
                logger.w(TAG, "activate:failure result=$result", error)
                return@withContext Result.failure(error)
            }
            val code = resultCode.toLongOrNull()
            if (code == null) {
                val error = IllegalArgumentException("Unexpected result code '$resultCode'")
                logger.w(TAG, "activate:unexpectedCode", error)
                return@withContext Result.failure(error)
            }

            if (code <= RESULT_CODE_SUCCESS) {
                val error = RuntimeException("Activation failed with code $code")
                logger.w(TAG, "activate:invalidCode", error)
                return@withContext Result.failure(error)
            }

            logger.d(TAG, "activate:success resultCode=$resultCode")
            // ignoring the result
            // if we can't save the card, we don't have a way to rollback, as the network request
            // already succeeded
            cardsRepository.saveCard(
                card.copy(
                    state = CardState.Activated,
                    activatedAt = Clock.System.now()
                )
            )
            return@withContext Result.success(Unit)
        }

    companion object {
        private val TAG = ActivateCardUseCaseImpl::class
        private const val RESULT_CODE_SUCCESS = 277028
    }
}