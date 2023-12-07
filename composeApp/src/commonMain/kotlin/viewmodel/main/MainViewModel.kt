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
import usecase.DeleteItemUseCase
import usecase.GetFilteredTagsUseCase
import usecase.ObserveLinkPropertiesUseCase
import usecase.ToggleFavouriteItemUseCase
import viewmodel.MainScreenState
import viewmodel.ViewModel

class MainViewModel(
    private val observeLinkPropertiesUseCase: ObserveLinkPropertiesUseCase,
    private val toggleFavouriteItemUseCase: ToggleFavouriteItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val getFilteredTagsUseCase: GetFilteredTagsUseCase
) : ViewModel<MainScreenState> {
    private val viewModelJob = SupervisorJob()
    override val viewModelScope: CoroutineScope =
        CoroutineScope(viewModelJob + Dispatchers.Main.immediate)
    private val linkSearch = MutableSharedFlow<Search>()
    private val errorOrIdleState = MutableStateFlow<MainScreenState?>(null)
    private val search = mutableStateOf(Search())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state: StateFlow<MainScreenState> =
        linkSearch.flatMapLatest { linkSearchOperation ->
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

    init {
        viewModelScope.launch { linkSearch.emit(search.value) }
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
