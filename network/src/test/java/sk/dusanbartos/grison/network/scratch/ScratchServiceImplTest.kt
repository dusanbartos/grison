package sk.dusanbartos.grison.network.scratch

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.logger.Logger
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchServiceImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private lateinit var service: ScratchServiceImpl

    @Before
    fun setup() {
        service = ScratchServiceImpl(logger)
    }

    @Test
    fun `scratchCard - should return valid UUID after delay`() = runTest {
        // When
        var completed = false
        val job = launch {
            val result = service.scratchCard()
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
            assertTrue(result.getOrNull()?.matches(Regex("[0-9a-f-]+")) ?: false)
            completed = true
        }

        // Then
        advanceTimeBy(1.seconds)
        assertFalse(completed)

        advanceUntilIdle()
        assertTrue(completed)
        job.join()
    }

    @Test
    fun `scratchCard - multiple calls should return different UUIDs`() = runTest {
        // When
        val result1 = service.scratchCard()
        val result2 = service.scratchCard()

        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertNotEquals(result1.getOrNull(), result2.getOrNull())
    }
}