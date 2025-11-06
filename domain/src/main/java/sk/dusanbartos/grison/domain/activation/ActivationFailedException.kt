package sk.dusanbartos.grison.domain.activation

class ActivationFailedException(override val cause: Throwable) :
    Exception("Activation failed - ${cause.message}")