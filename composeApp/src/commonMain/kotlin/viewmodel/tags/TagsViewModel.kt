package viewmodel.tags

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import model.LinkProperty
import model.LinkTagOperation
import usecase.GetFilteredTagsUseCase
import usecase.GetLinkPropertyUseCase
import usecase.UpdateTagForLinkPropertyUseCase
import viewmodel.ViewModel

class TagsViewModel(
    getFilteredTagsUseCase: GetFilteredTagsUseCase,
    private val updateTagForLinkPropertyUseCase: UpdateTagForLinkPropertyUseCase,
    private val linkPropertyToModify: LinkProperty,
    private val getLinkPropertyByIdUseCase: GetLinkPropertyUseCase
) : ViewModel {
    override val viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val searchTrigger = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = searchTrigger.flatMapLatest { query ->
        combine(
            getFilteredTagsUseCase(query),
            getLinkPropertyByIdUseCase(linkPropertyToModify)
        ) { tags, linkProperty ->
            Pair(tags, linkProperty)
        }
    }.map { pair ->
        TagsState.Success(pair.first.map { it.name }, pair.second)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TagsState.Idle)

    init {
        viewModelScope.launch { searchTrigger.emit("") }
    }

    sealed class TagsState {
        data object Idle : TagsState()
        data class Success(val tags: List<String>, val linkProperty: LinkProperty) : TagsState()
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
