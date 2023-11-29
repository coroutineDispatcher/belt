package viewmodel

import kotlinx.coroutines.CoroutineScope

interface ViewModel {
    val viewModelScope: CoroutineScope
    fun dispose()
    fun backToIdle()
}
