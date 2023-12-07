package viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface ViewModel<T : ScreenState> {
    val state: StateFlow<T>
    val viewModelScope: CoroutineScope
    fun dispose()
    fun backToIdle()
}
