import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
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

    actual fun openLink(url: String) {
        val safariViewController = SFSafariViewController(uRL = NSURL(string = url))
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            safariViewController,
            animated = true,
            null
        )
    }
}

actual val linkManager = LinkManager()
