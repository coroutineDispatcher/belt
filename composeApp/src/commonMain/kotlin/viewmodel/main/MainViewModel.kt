package viewmodel.main

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.LinkProperty
import model.LinkSearchProperty
import model.Search
import usecase.AddUrlToDatabaseUseCase
import usecase.DeleteItemUseCase
import usecase.GetFilteredTagsUseCase
import usecase.IsValidUrlUseCase
import usecase.ObserveLinkPropertiesUseCase
import usecase.ToggleFavouriteItemUseCase
import viewmodel.ViewModel

class MainViewModel(
    private val addUrlToDbUseCase: AddUrlToDatabaseUseCase,
    private val isValidURLUseCase: IsValidUrlUseCase,
    private val observeLinkPropertiesUseCase: ObserveLinkPropertiesUseCase,
    private val toggleFavouriteItemUseCase: ToggleFavouriteItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val getFilteredTagsUseCase: GetFilteredTagsUseCase
) : ViewModel {
    private val viewModelJob = SupervisorJob()
    override val viewModelScope: CoroutineScope =
        CoroutineScope(viewModelJob + Dispatchers.Main.immediate)
    private val linkSearch = MutableSharedFlow<Search>()
    private val errorOrIdleState = MutableStateFlow<MainScreenState?>(null)
    private val search = mutableStateOf(Search())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MainScreenState> = linkSearch.flatMapLatest { linkSearchOperation ->
        combine(
            getFilteredTagsUseCase(""),
            observeLinkPropertiesUseCase(linkSearchOperation),
            errorOrIdleState
        ) { tags, linkProperties, errorOrIdleState ->
            Triple(tags, linkProperties, errorOrIdleState)
        }
    }.map { triple ->
        if (triple.third != null) {
            checkNotNull(triple.third)
        } else {
            MainScreenState.Success(
                tags = triple.first.map { it.name },
                linkProperties = triple.second
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainScreenState.Idle)

    sealed class MainScreenState {
        data object Idle : MainScreenState()
        data object Failure : MainScreenState()
        data object InvalidUrl : MainScreenState()
        data class Success(val tags: List<String>, val linkProperties: List<LinkProperty>) :
            MainScreenState()

        data object Empty : MainScreenState()
    }

    init {
        viewModelScope.launch { linkSearch.emit(search.value) }
    }

    fun validateAndGetMetadata(url: String) {
        viewModelScope.launch {
            if (!isValidURLUseCase(url)) {
                errorOrIdleState.emit(MainScreenState.InvalidUrl)
            } else {
                try {
                    addUrlToDbUseCase(url)
                } catch (exception: Exception) {
                    errorOrIdleState.emit(MainScreenState.Failure)
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
        viewModelScope.launch { errorOrIdleState.emit(MainScreenState.Idle) }
    }

    fun searchByQuery(searchQuery: String) {
        search.value = search.value.copy(searchQuery = searchQuery)
        viewModelScope.launch { linkSearch.emit(search.value) }
    }

    fun searchByTag(clickedTag: String) {
        search.value = search.value.copy(tags = search.value.tags + clickedTag)
        viewModelScope.launch { linkSearch.emit(search.value) }
    }

    fun removeTagFromFilter(tag: String) {
        search.value = search.value.copy(tags = search.value.tags - tag)
        viewModelScope.launch { linkSearch.emit(search.value) }
    }

    fun searchByLinkProperty(linkSearchProperty: LinkSearchProperty) {
        search.value = search.value.copy(property = linkSearchProperty)
        viewModelScope.launch { linkSearch.emit(search.value) }
    }

    fun clearSearchProperty() {
        search.value = search.value.copy(property = LinkSearchProperty.None)
        viewModelScope.launch { linkSearch.emit(search.value) }
    }
}
