package repository

import datasource.LinkDatasource
import model.LinkProperty

class LinksRepository(
    private val linksDatasource: LinkDatasource
) {
    suspend fun tryAddToDb(url: String) = linksDatasource.tryAddToDb(url)
    fun isValidUrl(url: String): Boolean {
        val urlRegex =
            Regex("""^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$""")
        return url.matches(urlRegex)
    }

    suspend fun toggleFavouriteItem(linkProperty: LinkProperty) =
        linksDatasource.toggleFavouriteItem(linkProperty)

    suspend fun deleteItem(linkProperty: LinkProperty) = linksDatasource.deleteItem(linkProperty)

    val linkPropertiesObserver = linksDatasource.databaseObservable
}