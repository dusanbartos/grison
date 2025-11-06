package sk.dusanbartos.grison.network.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import sk.dusanbartos.grison.domain.Environment
import sk.dusanbartos.grison.domain.activation.ActivationService
import sk.dusanbartos.grison.domain.scratch.ScratchService
import sk.dusanbartos.grison.network.activation.ActivationApi
import sk.dusanbartos.grison.network.activation.ActivationServiceImpl
import sk.dusanbartos.grison.network.scratch.ScratchServiceImpl
import javax.inject.Singleton

@Module(
    includes = [
        HiltNetworkProviders::class,
        HiltNetworkBinders::class,
    ]
)
@DisableInstallInCheck
object NetworkModule

@Module
@DisableInstallInCheck
object HiltNetworkProviders {
    @Provides
    @Singleton
    internal fun providesActivationApi(
        environment: Environment,
    ): ActivationApi {
        val json = Json {
            ignoreUnknownKeys = true
        }

        val okHttpClient = OkHttpClient.Builder()
            .let {
                if (environment.isNetworkLoggingEnabled) {
                    it.addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                } else {
                    it
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(environment.activationUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
            .create(ActivationApi::class.java)
    }
}

@Module
@DisableInstallInCheck
abstract class HiltNetworkBinders {
    @Binds
    @Singleton
    internal abstract fun bindsActivationService(impl: ActivationServiceImpl): ActivationService

    @Binds
    @Singleton
    internal abstract fun bindsScratchService(impl: ScratchServiceImpl): ScratchService
}