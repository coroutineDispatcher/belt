import android.content.Context
import android.content.Intent
import android.net.Uri
import org.playgrounddispatcher.MainActivity

actual class LinkManager(private val activityContext: Context) {
    actual fun shareLink(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        activityContext.startActivity(intent)
    }

    actual fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        activityContext.startActivity(intent)
    }
}

actual val linkManager: LinkManager by lazy {
    LinkManager(checkNotNull(MainActivity.instance?.baseContext))
}
