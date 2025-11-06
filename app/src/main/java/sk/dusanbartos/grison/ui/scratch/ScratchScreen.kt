package sk.dusanbartos.grison.ui.scratch

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import sk.dusanbartos.grison.NavActions
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.ui.composables.EmptyState
import sk.dusanbartos.grison.ui.composables.LoadingComponent
import sk.dusanbartos.grison.ui.composables.ScratchCard
import sk.dusanbartos.grison.ui.composables.TopBar
import sk.dusanbartos.grison.ui.composables.TopBarRow
import sk.dusanbartos.grison.ui.composables.TopBarSubtitle
import sk.dusanbartos.grison.ui.composables.TopBarTitle
import sk.dusanbartos.grison.ui.scratch.ScratchScreenEvent.GoToActivation
import sk.dusanbartos.grison.ui.scratch.ScratchScreenEvent.OpenActivationProcessed
import sk.dusanbartos.grison.ui.scratch.ScratchScreenEvent.Scratch
import sk.dusanbartos.grison.ui.theme.GrisonPreview
import kotlin.time.Instant

@Composable
fun ScratchRoute(
    viewModel: ScratchViewModel,
    navActions: NavActions,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state.openActivation != null) {
            viewModel.onEvent(OpenActivationProcessed)
            navActions.navigateToActivation()
        }
    }

    ScratchScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = navActions.goBack,
    )
}

@Composable
private fun ScratchScreen(
    state: ScratchScreenState,
    onEvent: (ScratchScreenEvent) -> Unit,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar {
            TopBarRow(onBack)
            TopBarTitle("Scratch the Card")
            TopBarSubtitle("Scratch the card first to unlock your reward")
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

                    if (state.isScratching) {
                        item { LoadingComponent() }
                        item { Text("Scratching, please wait...") }
                        return@LazyColumn
                    }

                    when (state.card.state) {
                        CardState.New -> {
                            item {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onEvent(Scratch) }
                                ) {
                                    Text("Scratch")
                                }
                            }
                        }
                        CardState.Scratched -> {
                            item {
                                Text("Before using the card, you need to activate the code")
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onEvent(GoToActivation) }
                                ) {
                                    Text("Go To Activation Screen")
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
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewNew() {
    GrisonPreview {
        ScratchScreen(
            state = ScratchScreenState(
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
private fun PreviewScratched() {
    GrisonPreview {
        ScratchScreen(
            state = ScratchScreenState(
                card = mockCard.copy(
                    state = CardState.Scratched,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c"
                ),
            ),
            onEvent = {},
            onBack = {},
        )
    }
}

// region mock data
private val mockCard = Card(
    uuid = "3f1b2a6e-8d4b-4c2a-9f3a-5b7e2c1a9d0f",
    state = CardState.New,
)
// endregion