package sk.dusanbartos.grison.cards.di

import dagger.Binds
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import sk.dusanbartos.grison.cards.CardsRepositoryImpl
import sk.dusanbartos.grison.cards.activation.ActivateCardUseCaseImpl
import sk.dusanbartos.grison.cards.scratch.ScratchCardUseCaseImpl
import sk.dusanbartos.grison.domain.activation.ActivateCardUseCase
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.scratch.ScratchCardUseCase
import javax.inject.Singleton

@Module
@DisableInstallInCheck
abstract class CardsModule {

    @Binds
    @Singleton
    internal abstract fun bindsCardsRepository(impl: CardsRepositoryImpl): CardsRepository

    @Binds
    @Singleton
    internal abstract fun bindsActivateCardUseCase(impl: ActivateCardUseCaseImpl): ActivateCardUseCase

    @Binds
    @Singleton
    internal abstract fun bindsScratchCardUseCase(impl: ScratchCardUseCaseImpl): ScratchCardUseCase
}