package sk.dusanbartos.grison.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import sk.dusanbartos.grison.BuildConfig
import sk.dusanbartos.grison.cards.di.CardsModule
import sk.dusanbartos.grison.domain.ApplicationScope
import sk.dusanbartos.grison.domain.Environment
import sk.dusanbartos.grison.domain.IoDispatcher
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.logger.TimberDebugLogger
import sk.dusanbartos.grison.network.di.NetworkModule
import sk.dusanbartos.grison.storage.di.StorageModule
import javax.inject.Singleton

@Module(
    includes = [
        CardsModule::class,
        NetworkModule::class,
        StorageModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    // !! Important - this is not a Singleton, so everytime you inject the scope,
    // you'll get a new instance
    @Provides
    @ApplicationScope
    fun provideApplicationScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CoroutineScope =
        CoroutineScope(SupervisorJob() + ioDispatcher)

    @Provides
    @Singleton
    fun provideLogger(): Logger =
        if (BuildConfig.DEBUG) TimberDebugLogger() else Logger.Empty

    @Provides
    @Singleton
    fun provideEnvironment(): Environment = Environment(
        isNetworkLoggingEnabled = BuildConfig.DEBUG,
        activationUrl = "https://api.o2.sk/",
    )
}