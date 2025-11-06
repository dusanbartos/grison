package sk.dusanbartos.grison.ui.activation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.dusanbartos.grison.NavActions
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.ui.activation.ActivationScreenEvent.Activate
import sk.dusanbartos.grison.ui.activation.ActivationScreenEvent.ActivationErrorProcessed
import sk.dusanbartos.grison.ui.composables.EmptyState
import sk.dusanbartos.grison.ui.composables.LoadingComponent
import sk.dusanbartos.grison.ui.composables.ScratchCard
import sk.dusanbartos.grison.ui.composables.TopBar
import sk.dusanbartos.grison.ui.composables.TopBarRow
import sk.dusanbartos.grison.ui.composables.TopBarSubtitle
import sk.dusanbartos.grison.ui.composables.TopBarTitle
import sk.dusanbartos.grison.ui.theme.GrisonPreview
import kotlin.time.Instant

@Composable
fun ActivationRoute(
    viewModel: ActivationViewModel,
    navActions: NavActions,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ActivationScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = navActions.goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivationScreen(
    state: ActivationScreenState,
    onEvent: (ActivationScreenEvent) -> Unit,
    onBack: () -> Unit,
) {
    var activationError by remember { mutableStateOf<Throwable?>(null) }

    LaunchedEffect(state) {
        state.activationError?.let { error ->
            onEvent(ActivationErrorProcessed)
            activationError = error
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar {
            TopBarRow(onBack)
            TopBarTitle("Activate the code")
            TopBarSubtitle("Before using the card, the code needs to be activated first")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                state.isLoading -> {
                    item { LoadingComponent() }
                }

                state.card != null -> {
                    item {
                        ScratchCard(card = state.card)
                    }

                    if (state.isActivating) {
                        item { LoadingComponent() }
                        item { Text("Activating, please wait...") }
                        return@LazyColumn
                    }

                    when (state.card.state) {
                        CardState.New -> {
                            item {
                                Text("Before activation, you need to scratch the code")
                            }
                        }
                        CardState.Scratched -> {
                            item {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onEvent(Activate) }
                                ) {
                                    Text("Activation")
                                }
                            }
                        }
                        CardState.Activated -> {
                            item {
                                Text("Card is already activated, enjoy.")
                            }
                        }
                        else -> {}
                    }
                }
                // there is no card, something went wrong, show generic error
                else -> {
                    item {
                        EmptyState(
                            imageVector = Icons.Rounded.WarningAmber,
                            title = "There is no card",
                            subtitle = "The app encountered an error and cannot load a card",
                        )
                    }
                }
            }
        }
    }

    activationError?.let { error ->
        ActivationErrorDialog(error) { activationError = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivationErrorDialog(
    error: Throwable,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Activation Error",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Icon(
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally),
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("There was an error during activation")
                    Text(error.message ?: "Unknown error")
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScratched() {
    GrisonPreview {
        ActivationScreen(
            state = ActivationScreenState(
                card = mockCard,
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActivated() {
    GrisonPreview {
        ActivationScreen(
            state = ActivationScreenState(
                card = mockCard.copy(
                    state = CardState.Activated,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c",
                    activatedAt = Instant.fromEpochMilliseconds(1762416993),
                ),
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewError() {
    GrisonPreview {
        ActivationErrorDialog(
            error = RuntimeException("Activation failed with code 123"),
            onDismiss = {},
        )
    }
}

// region mock data
private val mockCard = Card(
    uuid = "3f1b2a6e-8d4b-4c2a-9f3a-5b7e2c1a9d0f",
    state = CardState.Scratched,
    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c"
)
// endregion