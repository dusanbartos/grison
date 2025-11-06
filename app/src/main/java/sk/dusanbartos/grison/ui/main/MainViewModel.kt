package sk.dusanbartos.grison.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sk.dusanbartos.grison.domain.IoDispatcher
import sk.dusanbartos.grison.domain.cards.CardState
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.ui.activation.ActivationViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cardsRepository: CardsRepository,
    private val logger: Logger,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            cardsRepository.streamCard().collect { card ->
                logger.d(TAG, "streamCard:next $card")
                _state.update { it.copy(isLoading = false, card = card) }
            }
        }
    }

    fun onEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.GoToActivation ->
                _state.update { it.copy(openActivation = Unit) }
            MainScreenEvent.GoToScratch ->
                _state.update { it.copy(openScratch = Unit) }
            MainScreenEvent.OpenActivationProcessed ->
                _state.update { it.copy(openActivation = null) }
            MainScreenEvent.OpenScratchProcessed ->
                _state.update { it.copy(openScratch = null) }

            MainScreenEvent.Reset -> reset()
        }
    }

    /**
     * This is purposely not wrapped in a UseCase, as it's only used for demo purposes
     * In real life, this would follow the same pattern as other UseCases.
     * Also it would only be available in debug builds, guarded by BuildConfig flag or by other means
     */
    private fun reset() {
        val state = _state.value

        if (state.card == null) {
            logger.w(TAG, "Reset is not available without card info")
            return
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            try {
                logger.d(TAG, "reset:start")
                // ignoring result handler, as it's only for demo purposes
                cardsRepository.saveCard(
                    state.card.copy(
                        state = CardState.New,
                        activationCode = null,
                        scratchedAt = null,
                        activatedAt = null,
                    )
                )
            } catch (e: Throwable) {
                logger.e(TAG, "reset:error", e)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        private val TAG = MainViewModel::class
    }
}