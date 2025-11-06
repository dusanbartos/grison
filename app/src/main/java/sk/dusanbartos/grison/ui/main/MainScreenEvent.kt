package sk.dusanbartos.grison.ui.main

sealed interface MainScreenEvent {
    data object GoToScratch : MainScreenEvent
    data object GoToActivation : MainScreenEvent
    data object OpenScratchProcessed : MainScreenEvent
    data object OpenActivationProcessed : MainScreenEvent
    data object Reset : MainScreenEvent
}