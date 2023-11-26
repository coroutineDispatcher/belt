package viewmodel.main

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
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
    val state = mutableStateOf<MainScreenState>(MainScreenState.Idle)

    sealed class MainScreenState {
        data object Idle : MainScreenState()
        data object Failure : MainScreenState()
        data object InvalidUrl : MainScreenState()
        data class Success(val linkProperty: List<LinkProperty>) : MainScreenState()
    }

    init {
        viewModelScope.launch {
            observeLinkPropertiesUseCase().collectLatest { linkPropertiesResult ->
                state.value = MainScreenState.Success(linkPropertiesResult)
            }
        }
    }

    fun validateAndGetMetadata(url: String) {
        viewModelScope.launch {
            if (!isValidURLUseCase(url)) {
                state.value = MainScreenState.InvalidUrl
            } else {
                try {
                    addUrlToDbUseCase(url)
                } catch (exception: Exception) {
                    state.value = MainScreenState.Failure
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
}
