package viewmodel.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.LinkProperty
import usecase.AddUrlToDatabaseUseCase
import usecase.DeleteItemUseCase
import usecase.IsValidUrlUseCase
import usecase.ObserveLinkPropertiesUseCase
import usecase.ToggleFavouriteItemUseCase
import viewmodel.ViewModel

class MainViewModel(
    private val addUrlToDbUseCase: AddUrlToDatabaseUseCase,
    private val isValidURLUseCase: IsValidUrlUseCase,
    private val observeLinkPropertiesUseCase: ObserveLinkPropertiesUseCase,
    private val toggleFavouriteItemUseCase: ToggleFavouriteItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel {

    private val viewModelJob = SupervisorJob()
    override val viewModelScope: CoroutineScope =
        CoroutineScope(viewModelJob + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Idle)
    val state = _state.asStateFlow()

    sealed class MainScreenState {
        data object Idle : MainScreenState()
        data object Failure : MainScreenState()
        data object InvalidUrl : MainScreenState()
        data class Success(val linkProperties: List<LinkProperty>) : MainScreenState()
        data object Empty : MainScreenState()
    }

    init {
        viewModelScope.launch {
            observeLinkPropertiesUseCase().collectLatest { linkPropertiesResult ->
                _state.update { MainScreenState.Success(linkPropertiesResult) }
            }
        }
    }

    fun validateAndGetMetadata(url: String) {
        viewModelScope.launch {
            if (!isValidURLUseCase(url)) {
                _state.update { MainScreenState.InvalidUrl }
            } else {
                try {
                    addUrlToDbUseCase(url)
                } catch (exception: Exception) {
                    _state.update { MainScreenState.Failure }
                }
            }
        }
    }

    override fun dispose() {
        viewModelJob.cancel()
    }

    fun toggleFavorite(item: LinkProperty) {
        viewModelScope.launch { toggleFavouriteItemUseCase(item) }
    }

    fun deleteItem(item: LinkProperty) {
        viewModelScope.launch { deleteItemUseCase(item) }
    }

    override fun backToIdle() {
        _state.update { MainScreenState.Idle }
    }
}
