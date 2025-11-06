package sk.dusanbartos.grison.ui.activation

sealed interface ActivationScreenEvent {
    data object Activate : ActivationScreenEvent
    data object ActivationErrorProcessed : ActivationScreenEvent
}