package sk.dusanbartos.grison.domain.activation

import sk.dusanbartos.grison.domain.cards.Card

interface ActivateCardUseCase {
    suspend operator fun invoke(
        card: Card,
        activationCode: String,
    ): Result<Unit>
}