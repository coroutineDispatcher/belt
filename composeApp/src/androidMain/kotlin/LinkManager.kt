import android.content.Context
import android.content.Intent
import android.net.Uri

actual class LinkManager {
    private var context: Context? = null

    actual fun shareLink(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        checkNotNull(context).startActivity(intent)
    }

    actual fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        checkNotNull(context).startActivity(intent)
    }

    fun setUp(context: Context) {
        this.context = context
    }
}

// TODO SOLVE THIS PROBLEM
actual val linkManager = LinkManager()
