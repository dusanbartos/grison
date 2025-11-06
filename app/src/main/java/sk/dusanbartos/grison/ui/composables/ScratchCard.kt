package sk.dusanbartos.grison.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.ui.theme.GrisonPreview
import sk.dusanbartos.grison.ui.theme.disabledContentAlpha
import sk.dusanbartos.grison.ui.theme.mediumContentAlpha
import kotlin.time.Instant

@Composable
fun ScratchCard(
    card: Card,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Card Id:",
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(disabledContentAlpha)
            )
            Text(text = card.uuid)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Card State:",
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(disabledContentAlpha)
            )
            Text(
                text = card.state.name,
                style = MaterialTheme.typography.titleLarge,
                color = when (card.state) {
                    CardState.Activated -> MaterialTheme.colorScheme.tertiary
                    else -> LocalContentColor.current
                },
                fontWeight = when (card.state) {
                    CardState.Activated -> FontWeight.Bold
                    else -> FontWeight.Normal
                }
            )

            card.activationCode?.let { activationCode ->
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Here is your Activation Code:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(mediumContentAlpha)
                )

                Text(
                    text = activationCode,
                    style = MaterialTheme.typography.titleLarge,
                    color = when (card.state) {
                        CardState.Activated -> MaterialTheme.colorScheme.tertiary
                        else -> LocalContentColor.current.copy(alpha = disabledContentAlpha)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    GrisonPreview {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScratchCard(
                card = mockCard
            )

            ScratchCard(
                card = mockCard.copy(
                    state = CardState.Scratched,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c"
                )
            )

            ScratchCard(
                card = mockCard.copy(
                    state = CardState.Activated,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c",
                    activatedAt = Instant.fromEpochMilliseconds(1762416993),
                )
            )
        }
    }
}

// region mock data
private val mockCard = Card(
    uuid = "3f1b2a6e-8d4b-4c2a-9f3a-5b7e2c1a9d0f",
    state = CardState.New,
)
// endregion