package sk.dusanbartos.grison.network.activation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivationResponse(
    @SerialName("android")
    val android: String
)
