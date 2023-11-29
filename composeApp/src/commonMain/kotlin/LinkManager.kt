expect class LinkManager {
    fun shareLink(url: String)
    fun openLink(url: String)
}

expect val linkManager: LinkManager
