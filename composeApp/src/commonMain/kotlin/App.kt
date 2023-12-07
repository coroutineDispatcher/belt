import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.collectLatest
import navigation.BackStackHandler
import navigation.Navigation
import theme.AppTheme
import ui.AddNewLinkScreen
import ui.MainScreen
import ui.TagsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(onAppExit: () -> Unit) {
    val backStackHandler = remember { BackStackHandler(initialScreen = Navigation.MainScreen) }
    val eventBus = remember { EventBus }
    val navigationState = backStackHandler.navigation.collectAsState()

    LaunchedEffect(eventBus) {
        eventBus.events.collectLatest { event ->
            when (event) {
                EventBus.Event.OnBackPressed -> {
                    if (platform() == iOS && backStackHandler.initialState()) return@collectLatest
                    backStackHandler.popBackStack()
                }
            }
        }
    }

    AppTheme {
        Surface {
            AnimatedContent(
                targetState = navigationState.value,
                transitionSpec = {
                    if (targetState == backStackHandler.initialScreen) {
                        slideInHorizontally(initialOffsetX = { width -> -width }) + fadeIn() togetherWith slideOutHorizontally(
                            targetOffsetX = { width -> width }
                        ) + fadeOut()
                    } else {
                        slideInHorizontally(initialOffsetX = { width -> width }) +
                            fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { width -> -width }) + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) {
                when (val state = navigationState.value) {
                    Navigation.MainScreen -> MainScreen(backStackHandler)
                    is Navigation.TagsScreen -> TagsScreen(state.linkToModify, backStackHandler)
                    Navigation.AddNewLinkScreen -> AddNewLinkScreen(backStackHandler)
                    null -> onAppExit()
                }
            }
        }
    }
}
