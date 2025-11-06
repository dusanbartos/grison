package sk.dusanbartos.grison.network.activation

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import sk.dusanbartos.grison.domain.activation.ActivationFailedException
import sk.dusanbartos.grison.domain.logger.Logger

class ActivationServiceImplTest {
    private val activationApi = mockk<ActivationApi>()
    private val logger = mockk<Logger>(relaxed = true)

    private lateinit var service: ActivationServiceImpl

    @Before
    fun setup() {
        service = ActivationServiceImpl(activationApi, logger)
    }

    @Test
    fun `activate - success case`() = runTest {
        // Given
        val activationCode = "000000"
        val response = ActivationResponse(android = "277029")
        coEvery { activationApi.activate(activationCode) } returns response

        // When
        val result = service.activateCard(activationCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("277029", result.getOrNull())
    }

    @Test
    fun `activate - api failure should wrap in ActivationFailedException`() = runTest {
        // Given
        val activationCode = "000000"
        val apiError = RuntimeException("Network error")
        coEvery { activationApi.activate(activationCode) } throws apiError

        // When
        val result = service.activateCard(activationCode)

        // Then
        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is ActivationFailedException)
        assertEquals(apiError, error?.cause)
    }
}