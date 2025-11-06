package sk.dusanbartos.grison.network.scratch

import kotlinx.coroutines.delay
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.domain.scratch.ScratchService
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class ScratchServiceImpl @Inject constructor(
    private val logger: Logger,
) : ScratchService {

    override suspend fun scratchCard(): Result<String> {
        logger.d(TAG, "scratchCard:start")

        try {
            // [ASS-3]
            // simulating a network request by delaying the result
            delay(2.seconds)
            val generatedId = Uuid.random().toHexDashString()
            logger.d(TAG, "scratchCard:success")
            return Result.success(generatedId)
        } catch (e: Throwable) {
            logger.e(TAG, "scratchCard:error", e)
            return Result.failure(e)
        }
    }

    companion object {
        private val TAG = ScratchServiceImpl::class
    }
}