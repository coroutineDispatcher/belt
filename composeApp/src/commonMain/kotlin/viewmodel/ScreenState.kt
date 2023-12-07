package viewmodel

import model.LinkProperty

sealed interface ScreenState

sealed class NewLinkPropertyState : ScreenState {
    data object Idle : NewLinkPropertyState()
    data object InvalidUrl : NewLinkPropertyState()

    data object Failure : NewLinkPropertyState()

    data object Success : NewLinkPropertyState()
}

sealed class MainScreenState : ScreenState {
    data object Idle : MainScreenState()
    data class Success(val tags: List<String>, val linkProperties: List<LinkProperty>) :
        MainScreenState()

    data object Empty : MainScreenState()
}

sealed class TagsState : ScreenState {
    data object Idle : TagsState()
    data class Success(val tags: List<String>, val linkProperty: LinkProperty) : TagsState()
    data object Finish : TagsState()
}
