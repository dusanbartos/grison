package sk.dusanbartos.grison

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Main : AppRoute

    // [ASS-1]
    // for multi-card support, we would need to pass UUID via route params
//    @Serializable
//    data class Scratch(val cardId: String) : AppRoute
    @Serializable
    data object Scratch : AppRoute

    // [ASS-1]
    // for multi-card support, we would need to pass UUID via route params
//    @Serializable
//    data class Activation(val cardId: String) : AppRoute
    @Serializable
    data object Activation : AppRoute
}