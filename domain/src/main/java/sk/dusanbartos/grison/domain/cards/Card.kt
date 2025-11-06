package sk.dusanbartos.grison.domain.cards

import kotlin.time.Instant

data class Card(
    // unique card identifier [ASS-2]
    val uuid: String,
    val state: CardState,
    // activation code is essentially mutable based on [ASS-3]
    val activationCode: String? = null,
    val scratchedAt: Instant? = null,
    val activatedAt: Instant? = null,
) {
    companion object {
        val EMPTY = Card(
            uuid = "",
            state = CardState.Unknown,
        )
    }
}