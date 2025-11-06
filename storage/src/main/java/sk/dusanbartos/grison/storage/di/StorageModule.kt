package sk.dusanbartos.grison.storage.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.migration.DisableInstallInCheck
import sk.dusanbartos.grison.domain.cards.CardsStore
import sk.dusanbartos.grison.storage.GrisonDatabase
import sk.dusanbartos.grison.storage.cards.CardsDao
import sk.dusanbartos.grison.storage.cards.RoomCardsStore
import javax.inject.Singleton

@Module(
    includes = [
        HiltStorageProviders::class,
        HiltStorageBinders::class,
    ]
)
@DisableInstallInCheck
object StorageModule

@Module
@DisableInstallInCheck
object HiltStorageProviders {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GrisonDatabase =
        Room.databaseBuilder(context, GrisonDatabase::class.java, "grison.db")
            .build()

    @Provides
    @Singleton
    fun provideCardsDao(database: GrisonDatabase): CardsDao = database.cardsDao()
}

@Module
@DisableInstallInCheck
abstract class HiltStorageBinders {
    @Binds
    @Singleton
    internal abstract fun bindsCardStore(impl: RoomCardsStore): CardsStore
}