package sk.dusanbartos.grison.storage.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.dusanbartos.grison.domain.cards.CardState
import kotlin.time.Instant

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "state")
    val state: CardState,

    @ColumnInfo(name = "activation_code")
    val activationCode: String?,

    @ColumnInfo(name = "scratched_at")
    val scratchedAt: Instant? = null,

    @ColumnInfo(name = "activated_at")
    val activatedAt: Instant? = null,
)
