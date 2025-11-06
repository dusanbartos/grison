package sk.dusanbartos.grison.ui.main

import sk.dusanbartos.grison.domain.cards.Card

data class MainScreenState(
    val isLoading: Boolean = false,
    val card: Card? = null,
    val openScratch: Unit? = null,
    val openActivation: Unit? = null,
)
