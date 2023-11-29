@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import model.LinkProperty
import org.jetbrains.compose.resources.ExperimentalResourceApi
import theme.AppTheme
import ui.BeltDialog
import ui.LinkPropertyListItem
import viewmodel.main.MainViewModel

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun App() {
    val viewModel = remember { BeltAppDI.mainViewModel }
    val linkManager = remember { linkManager }
    var url by remember { mutableStateOf("") }
    var data by remember { mutableStateOf(emptyList<LinkProperty>()) }
    val state = viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }
    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        placeholder = {
                            Text(text = "Place your link here")
                        },
                        modifier = Modifier.height(50.dp).weight(2f)
                    )
                    TextButton(
                        onClick = {
                            viewModel.validateAndGetMetadata(url)
                            url = ""
                        },
                        modifier = Modifier.wrapContentHeight().fillMaxWidth()
                            .align(Alignment.CenterVertically).weight(1f)
                            .padding(4.dp)
                    ) {
                        Text("Add")
                    }
                }
            }
        }) { padding ->
            AnimatedVisibility(
                data.isNotEmpty(),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                LazyColumn {
                    items(data) { item ->
                        LinkPropertyListItem(
                            item,
                            onFavoriteClick = { favoriteItem ->
                                viewModel.toggleFavorite(favoriteItem)
                            },
                            onShareClick = { itemToShare ->
                                linkManager.shareLink(itemToShare.url)
                            },
                            onMoreClicked = { itemReadyForOptions ->
                                // TODO Options
                            },
                            onItemClick = { itemToOpen -> linkManager.openLink(itemToOpen.url) }
                        )
                    }
                }
            }

            when (val currentState = state.value) {
                is MainViewModel.MainScreenState.Success -> data = currentState.linkProperties
                MainViewModel.MainScreenState.Failure -> {
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

                MainViewModel.MainScreenState.InvalidUrl -> {
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

                MainViewModel.MainScreenState.Idle -> Unit
                MainViewModel.MainScreenState.Empty -> Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxSize()
                ) {
                    Text("Implement empty state", color = Color.Black)
                }
            }
        }
    }
}
