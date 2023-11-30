@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import di.BeltAppDI
import ui.MainScreen
import ui.TagsScreen

@Composable
fun App() {
    val viewModel = remember { BeltAppDI.navigationViewModel }
    val navigationState = viewModel.navigationState.collectAsState()

    when (val state = navigationState.value) {
        Navigation.MainScreen -> MainScreen(
            onNavigateToTags = { linkProperty ->
                viewModel.navigateTo(
                    Navigation.TagsScreen(
                        linkProperty
                    )
                )
            }
        )

        is Navigation.TagsScreen -> TagsScreen(state.linkToModify)
    }
}
