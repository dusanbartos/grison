package sk.dusanbartos.grison.ui.scratch

sealed interface ScratchScreenEvent {
    data object Scratch : ScratchScreenEvent
    data object GoToActivation : ScratchScreenEvent
    data object OpenActivationProcessed : ScratchScreenEvent
}