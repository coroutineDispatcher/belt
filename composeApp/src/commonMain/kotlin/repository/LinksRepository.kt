package repository

import datasource.LinkDatasource
import datasource.TagsDatasource
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.filter
import model.LinkProperty
import model.LinkTagOperation
import model.Search

class LinksRepository(
    private val linksDatasource: LinkDatasource,
    private val tagsDatasource: TagsDatasource
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

    fun filteredTags(filter: String = "") = tagsDatasource.tagsObservable(filter)

    suspend fun updateTagForLinkProperty(
        linkProperty: LinkProperty,
        newTag: String,
        operation: LinkTagOperation
    ) {
        if (operation == LinkTagOperation.Add) {
            tagsDatasource.addNewTag(newTag)
        }
        linksDatasource.updateTag(linkProperty, newTag, operation)
    }

    fun getLinkPropertyByIdAsFlow(id: RealmUUID) = linksDatasource.getPropertyById(id)

    fun linkPropertiesObserver(search: Search) = linksDatasource.linkPropertiesDatabaseObservable(search)
}
