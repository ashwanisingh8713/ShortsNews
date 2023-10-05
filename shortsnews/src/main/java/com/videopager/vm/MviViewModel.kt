package com.videopager.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal abstract class MviViewModel<Event, Result, State, Effect>(private val initialState: State) : ViewModel() {
    var states: StateFlow<State> = MutableStateFlow<State>(initialState)
    var effects: Flow<Effect> = emptyFlow()
    private val events = MutableSharedFlow<Event>()

    init {
        events
            .onSubscription {
                check(events.subscriptionCount.value == 1)
                onStart()
            }
            .share() // Share emissions to individual Flows within toResults()
            .toResults()
            .share() // Share emissions to states and effects
            .also { results ->
                states = results.toStates(initialState)
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.Lazily,
                        initialValue = initialState
                    )
                effects = results.toEffects()
            }
    }

    fun processEvent(event: Event) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun <T> Flow<T>.share(): Flow<T> {
        return shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily
        )
    }

    protected open fun onStart() = Unit
    protected abstract fun Flow<Event>.toResults(): Flow<Result>
    protected abstract fun Result.reduce(state: State): State
    protected open fun Flow<Result>.toEffects(): Flow<Effect> = emptyFlow()

    private fun Flow<Result>.toStates(initialState: State): Flow<State> {
        return scan(initialState) { state, result -> result.reduce(state) }
    }
}
