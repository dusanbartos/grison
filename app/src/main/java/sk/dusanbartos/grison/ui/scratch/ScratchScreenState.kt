package sk.dusanbartos.grison.ui.scratch

import sk.dusanbartos.grison.domain.cards.Card

data class ScratchScreenState(
    val isLoading: Boolean = false,
    val isScratching: Boolean = false,
    val card: Card? = null,
    val openActivation: Unit? = null,
)
