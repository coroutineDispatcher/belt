import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.collectLatest
import navigation.BackStackHandler
import navigation.Navigation
import theme.AppTheme
import ui.MainScreen
import ui.TagsScreen

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
        when (val state = navigationState.value) {
            Navigation.MainScreen -> AnimatedVisibility(true, enter = slideInHorizontally()) {
                MainScreen(
                    onNavigateToTags = { linkProperty ->
                        backStackHandler.add(
                            Navigation.TagsScreen(
                                linkProperty
                            )
                        )
                    }
                )
            }

            is Navigation.TagsScreen -> {
                TagsScreen(state.linkToModify, onNavigateToMainScreen = {
                    backStackHandler.popToLast()
                })
            }

            null -> onAppExit()
        }
    }
}
