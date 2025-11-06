package sk.dusanbartos.grison.ui.activation

import sk.dusanbartos.grison.domain.cards.Card

data class ActivationScreenState(
    val isLoading: Boolean = false,
    val isActivating: Boolean = false,
    val card: Card? = null,
    val activationError: Throwable? = null,
)
