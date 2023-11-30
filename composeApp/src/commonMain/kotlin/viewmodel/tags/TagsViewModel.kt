package viewmodel.tags

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.LinkProperty
import model.LinkTagOperation
import usecase.GetFilteredTagsUseCase
import usecase.UpdateTagForLinkPropertyUseCase
import viewmodel.ViewModel

class TagsViewModel(
    getFilteredTagsUseCase: GetFilteredTagsUseCase,
    private val updateTagForLinkPropertyUseCase: UpdateTagForLinkPropertyUseCase
) : ViewModel {
    override val viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val searchTrigger = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = searchTrigger.flatMapLatest { query ->
        getFilteredTagsUseCase(query)
    }.map { tags ->
        println("ViewModel: $tags")
        TagsState.Success(tags)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TagsState.Idle)

    init {
        viewModelScope.launch { searchTrigger.emit("") }
    }

    sealed class TagsState {
        data object Idle : TagsState()
        data class Success(val tags: List<String>) : TagsState()
        data object Finish : TagsState()
    }

    override fun dispose() {
        viewModelScope.cancel()
    }

    override fun backToIdle() = Unit

    fun search(value: String) {
        viewModelScope.launch { searchTrigger.emit(value) }
    }

    fun addTagToItem(linkProperty: LinkProperty, tag: String, then: () -> Unit) {
        viewModelScope.launch {
            updateTagForLinkPropertyUseCase(linkProperty, tag, LinkTagOperation.Add)
            then()
        }
    }

    fun removeTagFromLinkProperty(linkToModify: LinkProperty, tagToRemove: String) {
        viewModelScope.launch {
            updateTagForLinkPropertyUseCase(linkToModify, tagToRemove, LinkTagOperation.Remove)
        }
    }
}
