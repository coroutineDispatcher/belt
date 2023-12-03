package model

data class Search(
    val searchQuery: String = "",
    val tags: List<String> = listOf(),
    val property: LinkSearchProperty = LinkSearchProperty.None
)
