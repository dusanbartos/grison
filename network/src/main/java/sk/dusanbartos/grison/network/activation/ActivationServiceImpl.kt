package sk.dusanbartos.grison.network.activation

import kotlinx.coroutines.delay
import sk.dusanbartos.grison.domain.activation.ActivationFailedException
import sk.dusanbartos.grison.domain.activation.ActivationService
import sk.dusanbartos.grison.domain.logger.Logger
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class ActivationServiceImpl @Inject constructor(
    private val activationApi: ActivationApi,
    private val logger: Logger,
) : ActivationService {
    override suspend fun activateCard(
        activationCode: String
    ): Result<String> {
        logger.d(TAG, "activate:start code=$activationCode")

        try {
            // simulate some delay to provide better requirement verification
            // users can click activate and have 2 seconds to leave the screen
            // to see if the operation finishes even though the wrapper scope
            // (Screen's viewModelScope) was cancelled
            delay(2.seconds)
            val response = activationApi.activate(activationCode)
            logger.d(TAG, "activate:success response=$response")
            return Result.success(response.android)
        } catch (e: Throwable) {
            logger.e(TAG, "activate:error", e)
            return Result.failure(ActivationFailedException(e))
        }
    }

    companion object {
        private val TAG = ActivationServiceImpl::class
    }
}