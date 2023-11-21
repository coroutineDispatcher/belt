package datasource

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.select.Elements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import model.LinkProperty

private const val PROPERTY_TITLE = "og:title"
private const val PROPERTY_IMAGE = "og:image"
private const val PROPERTY_URL = "og:url"

class LinkDatasource {
    suspend fun getLinkMetadata(url: String): LinkSearchResult = withContext(Dispatchers.IO) {
        try {
            val document = Ksoup.connect(url)

            var linkProperty = LinkProperty(
                title = document.title(),
                image = null,
                url = url
            )
            val headlines: Elements = document.select("meta")

            headlines.forEach { element ->
                when (element.attr("property")) {
                    PROPERTY_IMAGE -> {
                        println("Extracting image: ${element.attr("content")}")
                        linkProperty = linkProperty.copy(image = element.attr("content"))
                    }

                    PROPERTY_URL -> {
                        println("Extracting url: ${element.attr("content")}")
                        linkProperty = linkProperty.copy(url = element.attr("content"))
                    }

                    else -> Unit
                }
            }

            return@withContext LinkSearchResult.Success(linkProperty)
        } catch (exception: Exception) {
            return@withContext LinkSearchResult.Failure
        }
    }

    fun isValidUrl(url: String): Boolean {
        val urlRegex = Regex("^(https?|ftp)://([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)\\.)*(([a-zA-Z0-9-]+\\.)+)[a-zA-Z]{2,}$")
        return urlRegex.matches(url)
    }

    sealed class LinkSearchResult {
        data object Failure : LinkSearchResult()
        data class Success(val linkProperty: LinkProperty) : LinkSearchResult()
    }
}