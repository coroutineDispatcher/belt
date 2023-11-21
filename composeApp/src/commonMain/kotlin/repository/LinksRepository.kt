package repository

import datasource.LinkDatasource

class LinksRepository(
    private val linksDatasource: LinkDatasource
) {
    suspend fun getLinkMetadata(url: String) = linksDatasource.getLinkMetadata(url)
    fun isValidUrl(url: String): Boolean {
        val urlRegex =
            Regex("""^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$""")
        return url.matches(urlRegex)
    }
}