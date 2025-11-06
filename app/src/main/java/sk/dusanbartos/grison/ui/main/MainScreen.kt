package sk.dusanbartos.grison.ui.main

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.dusanbartos.grison.NavActions
import sk.dusanbartos.grison.domain.cards.Card
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.ui.composables.EmptyState
import sk.dusanbartos.grison.ui.composables.LoadingComponent
import sk.dusanbartos.grison.ui.composables.ScratchCard
import sk.dusanbartos.grison.ui.composables.TopBar
import sk.dusanbartos.grison.ui.composables.TopBarTitle
import sk.dusanbartos.grison.ui.main.MainScreenEvent.GoToActivation
import sk.dusanbartos.grison.ui.main.MainScreenEvent.GoToScratch
import sk.dusanbartos.grison.ui.main.MainScreenEvent.OpenActivationProcessed
import sk.dusanbartos.grison.ui.main.MainScreenEvent.OpenScratchProcessed
import sk.dusanbartos.grison.ui.main.MainScreenEvent.Reset
import sk.dusanbartos.grison.ui.theme.GrisonPreview
import kotlin.time.Instant

@Composable
fun MainRoute(
    viewModel: MainViewModel,
    navActions: NavActions,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state.openScratch != null) {
            viewModel.onEvent(OpenScratchProcessed)
            navActions.navigateToScratch()
        }

        if (state.openActivation != null) {
            viewModel.onEvent(OpenActivationProcessed)
            navActions.navigateToActivation()
        }
    }

    MainScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar {
            TopBarTitle(text = "Hello User", modifier = Modifier.padding(top = 24.dp))
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

                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(GoToScratch) }
                        ) {
                            Text("Scratch")
                        }
                    }
                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(GoToActivation) }
                        ) {
                            Text("Activate")
                        }
                    }

                    item {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(Reset) }
                        ) {
                            Text("Reset")
                        }
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
        MainScreen(
            state = MainScreenState(
                card = mockCard,
            ),
            onEvent = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewScratched() {
    GrisonPreview {
        MainScreen(
            state = MainScreenState(
                card = mockCard.copy(
                    state = CardState.Scratched,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c"
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewActivated() {
    GrisonPreview {
        MainScreen(
            state = MainScreenState(
                card = mockCard.copy(
                    state = CardState.Activated,
                    scratchedAt = Instant.fromEpochMilliseconds(1762416928),
                    activationCode = "a6c3e9b2-4f7d-4a8b-915d-2c3f7b9e1a5c",
                    activatedAt = Instant.fromEpochMilliseconds(1762416993),
                ),
            ),
            onEvent = {},
        )
    }
}

// region mock data
private val mockCard = Card(
    uuid = "3f1b2a6e-8d4b-4c2a-9f3a-5b7e2c1a9d0f",
    state = CardState.New,
)
// endregion