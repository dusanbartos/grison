package sk.dusanbartos.grison.ui.scratch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sk.dusanbartos.grison.domain.IoDispatcher
import sk.dusanbartos.grison.domain.cards.CardsRepository
import sk.dusanbartos.grison.domain.logger.Logger
import sk.dusanbartos.grison.domain.scratch.ScratchCardUseCase
import javax.inject.Inject

@HiltViewModel
class ScratchViewModel @Inject constructor(
    //savedStateHandle: SavedStateHandle,
    private val cardsRepository: CardsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val scratchCardUseCase: ScratchCardUseCase,
    private val logger: Logger,
) : ViewModel() {

    private val _state = MutableStateFlow(ScratchScreenState())
    val state: StateFlow<ScratchScreenState> = _state.asStateFlow()

    init {
        // [ASS-1]
        // for multi-card support, we would need to pass UUID via route params
//        val routeParams = savedStateHandle.toRoute<AppRoute.Scratch>()
//        logger.d(ScratchViewModel::class, "init routeParams=$routeParams")

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            cardsRepository.streamCard().collect { card ->
                logger.d(TAG, "streamCard:next $card")
                _state.update { it.copy(isLoading = false, card = card) }
            }
        }
    }

    fun onEvent(event: ScratchScreenEvent) {
        when (event) {
            ScratchScreenEvent.Scratch -> scratch()
            ScratchScreenEvent.GoToActivation ->
                _state.update { it.copy(openActivation = Unit) }

            ScratchScreenEvent.OpenActivationProcessed ->
                _state.update { it.copy(openActivation = null) }
        }
    }

    private fun scratch() {
        val state = _state.value

        if (state.card == null) {
            logger.w(TAG, "Scratching is not available without card info")
            return
        }

        _state.update { it.copy(isScratching = true) }
        viewModelScope.launch(ioDispatcher) {
            try {
                val result = scratchCardUseCase.invoke(state.card)
                logger.d(TAG, "scratch:result $result")
            } catch (e: Throwable) {
                logger.e(TAG, "scratch:error", e)
            } finally {
                _state.update { it.copy(isScratching = false) }
            }
        }
    }

    companion object {
        private val TAG = ScratchViewModel::class
    }
}