package model

sealed class LinkSearchOperation {
    data class SearchByTitle(val query: String) : LinkSearchOperation()
    data class SearchByTag(val tag: String) : LinkSearchOperation()
}
