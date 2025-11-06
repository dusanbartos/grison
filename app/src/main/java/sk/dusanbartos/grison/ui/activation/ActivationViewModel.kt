package sk.dusanbartos.grison.ui.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sk.dusanbartos.grison.domain.ApplicationScope
import sk.dusanbartos.grison.domain.IoDispatcher
import sk.dusanbartos.grison.domain.activation.ActivateCardUseCase
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.ui.scratch.ScratchViewModel
import javax.inject.Inject

@HiltViewModel
class ActivationViewModel @Inject constructor(
    //savedStateHandle: SavedStateHandle,
    private val cardsRepository: CardsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val activateCardUseCase: ActivateCardUseCase,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(ActivationScreenState())
    val state: StateFlow<ActivationScreenState> = _state.asStateFlow()

    init {
        // [ASS-1]
        // for multi-card support, we would need to pass UUID via route params
        //val routeParams = savedStateHandle.toRoute<AppRoute.Activation>()
        //logger.d(TAG, "init routeParams=$routeParams")

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            cardsRepository.streamCard().collect { card ->
                logger.d(TAG, "streamCard:next $card")
                _state.update { it.copy(isLoading = false, card = card) }
            }
        }
    }

    fun onEvent(event: ActivationScreenEvent) {
        when (event) {
            ActivationScreenEvent.Activate -> activate()
            ActivationScreenEvent.ActivationErrorProcessed ->
                _state.update { it.copy(activationError = null) }
        }
    }

    private fun activate() {
        val state = _state.value

        if (state.card == null) {
            logger.w(TAG, "Activation is not available without card info")
            return
        }

        val activationCode = state.card.activationCode
        if (activationCode == null) {
            logger.w(TAG, "Activation is only available after card is scratched")
            return
        }

        _state.update { it.copy(isActivating = true) }
        viewModelScope.launch(ioDispatcher) {
            try {
                logger.d(TAG, "activate:start")
                val result = activateCardUseCase.invoke(state.card, activationCode)
                logger.d(TAG, "activate:result $result")
                result.exceptionOrNull()?.let { error ->
                    _state.update { it.copy(activationError = error) }
                }
            } catch (e: Throwable) {
                logger.e(TAG, "activate:error", e)
            } finally {
                _state.update { it.copy(isActivating = false) }
            }
        }
    }

    companion object {
        private val TAG = ActivationViewModel::class
    }
}