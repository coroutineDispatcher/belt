package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import navigation.BackStackHandler
import viewmodel.NewLinkPropertyState

@Composable
fun AddNewLinkScreen(
    backStackHandler: BackStackHandler
) {
    val viewModel = remember { BeltAppDI.newLinkViewModel() }
    val state = viewModel.state.collectAsState()
    var url by remember { mutableStateOf("") }

    Scaffold(
        topBar = { BeltSearchBar(url, "Search for URL") { text -> url = text } },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(16.dp).align(Alignment.Center).fillMaxWidth(),
                    onClick = { viewModel.validateAndGetMetadata(url) },
                    text = { Text("Search") },
                    icon = { Icon(Icons.Filled.Search, Icons.Filled.Search.name) }
                )
            }
        }
    ) {
        when (state.value) {
            NewLinkPropertyState.Failure -> {
                BeltDialog(
                    title = "Something went wrong",
                    content = "Something went wrong. Please try again.",
                    onDismiss = {
                        viewModel.backToIdle()
                    },
                    onConfirm = {
                        viewModel.backToIdle()
                    }
                )
            }

            NewLinkPropertyState.Idle -> Unit
            NewLinkPropertyState.InvalidUrl -> {
                BeltDialog(
                    title = "Invalid URL",
                    content = "The submitted URL is invalid. Please try another one.",
                    onDismiss = {
                        viewModel.backToIdle()
                    },
                    onConfirm = {
                        viewModel.backToIdle()
                    }
                )
            }

            NewLinkPropertyState.Success -> {
                backStackHandler.popBackStack()
            }
        }
    }
}
