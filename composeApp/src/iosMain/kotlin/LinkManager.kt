import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class LinkManager {
    actual fun shareLink(url: String) {
        val activityViewController = UIActivityViewController(listOf(url), null)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}

actual val linkManager = LinkManager()
