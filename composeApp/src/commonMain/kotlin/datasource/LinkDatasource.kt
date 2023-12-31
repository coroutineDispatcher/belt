package datasource

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import model.LinkProperty
import model.LinkSearchProperty
import model.LinkTagOperation
import model.Search

private const val PROPERTY_TITLE = "og:title"
private const val PROPERTY_IMAGE = "og:image"
private const val PROPERTY_URL = "og:url"

class LinkDatasource(
    private val httpClient: HttpClient,
    private val realm: Realm
) {
    fun linkPropertiesDatabaseObservable(search: Search): Flow<List<LinkProperty>> {
        return realm.query<LinkProperty>().asFlow()
            .map { changes ->
                changes.list.reversed().filter { item ->
                    val titleMatch = item.title.contains(search.searchQuery, ignoreCase = true)
                    val propertiesMatch = when (search.property) {
                        LinkSearchProperty.Favorite -> item.favorite
                        LinkSearchProperty.Tagged -> item.tags.isNotEmpty()
                        else -> true
                    }

                    titleMatch && item.tags.containsAll(search.tags) && propertiesMatch
                }
            }
    }

    fun getPropertyById(id: RealmUUID): Flow<LinkProperty> =
        realm.query<LinkProperty>("id == $0", id).find().asFlow().map { it.list.first() }

    suspend fun tryAddToDb(newUrl: String): Unit = withContext(Dispatchers.IO) {
        val response = httpClient.get(newUrl)

        val document = Ksoup.parse(response.body())
        val linkProperty = LinkProperty().apply {
            id = RealmUUID.random()
            title = document.title()
            image = null
            url = newUrl
            favorite = false
        }
        val headlines: Elements = document.select("meta")

        headlines.forEach { element ->
            when (element.attr("property")) {
                PROPERTY_IMAGE -> {
                    linkProperty.image = element.attr("content")
                }
                else -> Unit
            }
        }

        insertToDatabase(linkProperty)
    }

    private fun insertToDatabase(linkProperty: LinkProperty) {
        realm.writeBlocking {
            copyToRealm(linkProperty)
        }
    }

    suspend fun toggleFavouriteItem(linkProperty: LinkProperty) {
        val item = realm.query<LinkProperty>("id == $0", linkProperty.id).find().firstOrNull()
        realm.write {
            item?.let {
                findLatest(item)?.also {
                    it.favorite = !linkProperty.favorite
                    copyToRealm(it)
                }
            }
        }
    }

    suspend fun deleteItem(linkProperty: LinkProperty) {
        val item = realm.query<LinkProperty>("id == $0", linkProperty.id).find().firstOrNull()
        realm.write {
            item?.let { itemToDelete ->
                findLatest(itemToDelete)?.also { delete(it) }
            }
        }
    }

    suspend fun updateTag(linkProperty: LinkProperty, tag: String, operation: LinkTagOperation) {
        val item = realm.query<LinkProperty>("id == $0", linkProperty.id).find().firstOrNull()
        realm.write {
            item?.let {
                findLatest(item)?.also {
                    when (operation) {
                        LinkTagOperation.Add -> it.tags = (it.tags + tag).toRealmList()
                        LinkTagOperation.Remove -> it.tags = (it.tags - tag).toRealmList()
                    }

                    copyToRealm(it)
                }
            }
        }
    }
}
