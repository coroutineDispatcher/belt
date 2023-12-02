import androidx.compose.ui.window.ComposeUIViewController
import platform.posix.exit

fun MainViewController() = ComposeUIViewController { App { exit(-1) } }
