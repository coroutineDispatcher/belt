import model.LinkProperty

sealed class Navigation {
    data object MainScreen : Navigation()
    data class TagsScreen(val linkToModify: LinkProperty) : Navigation()
}
