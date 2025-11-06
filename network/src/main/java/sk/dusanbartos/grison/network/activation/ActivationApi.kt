package sk.dusanbartos.grison.network.activation

import retrofit2.http.GET
import retrofit2.http.Query

interface ActivationApi {
    @GET("/version")
    suspend fun activate(
        @Query(value = "code", encoded = true) activationCode: String
    ): ActivationResponse
}