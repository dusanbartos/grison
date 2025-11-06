package sk.dusanbartos.grison.domain.activation

interface ActivationService {
    suspend fun activateCard(
        activationCode: String
    ): Result<String>
}