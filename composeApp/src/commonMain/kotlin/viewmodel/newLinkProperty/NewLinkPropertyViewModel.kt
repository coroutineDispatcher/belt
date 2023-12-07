package viewmodel.newLinkProperty

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import usecase.AddUrlToDatabaseUseCase
import usecase.IsValidUrlUseCase
import viewmodel.NewLinkPropertyState
import viewmodel.ViewModel

class NewLinkPropertyViewModel(
    private val addUrlToDbUseCase: AddUrlToDatabaseUseCase,
    private val isValidURLUseCase: IsValidUrlUseCase
) : ViewModel<NewLinkPropertyState> {

    private val job = SupervisorJob()
    override val viewModelScope: CoroutineScope =
        CoroutineScope(job + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow<NewLinkPropertyState>(NewLinkPropertyState.Idle)
    override val state: StateFlow<NewLinkPropertyState> = _state.asStateFlow()

    override fun dispose() {
        viewModelScope.cancel()
    }

    override fun backToIdle() {
        viewModelScope.launch {
            _state.update { NewLinkPropertyState.Idle }
        }
    }

    fun validateAndGetMetadata(url: String) {
        viewModelScope.launch {
            if (!isValidURLUseCase(url)) {
                _state.update { NewLinkPropertyState.InvalidUrl }
            } else {
                try {
                    addUrlToDbUseCase(url)
                    _state.update { NewLinkPropertyState.Success }
                } catch (exception: Exception) {
                    _state.update { NewLinkPropertyState.Failure }
                }
            }
        }
    }
}
