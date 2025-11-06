package sk.dusanbartos.grison.domain.scratch

import sk.dusanbartos.grison.domain.cards.Card

interface ScratchCardUseCase {
    suspend operator fun invoke(card: Card): Result<Unit>
}