package ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import linkManager
import model.LinkProperty
import navigation.BackStackHandler
import navigation.Navigation
import viewmodel.main.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    backStackHandler: BackStackHandler
) {
    val viewModel = remember { BeltAppDI.mainViewModel() }
    val linkManager = remember { linkManager }
    var url by remember { mutableStateOf("") }
    var data by remember { mutableStateOf(emptyList<LinkProperty>()) }
    var searchQuery by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    var tags by remember { mutableStateOf(listOf<String>()) }
    val state = viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }

    key(searchQuery) {
        viewModel.searchByQuery(searchQuery)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
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
        },
        topBar = {
            Column(verticalArrangement = Arrangement.SpaceAround) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                            },
                            placeholder = {
                                Text(text = "Search saved links")
                            },
                            modifier = Modifier.height(56.dp).fillMaxWidth(),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Filled.Clear,
                                            "Clear Text"
                                        )
                                    }
                                }
                            },
                            interactionSource = interactionSource
                        )
                    }
                }

                LazyRow(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    items(tags) { currentTag ->
                        Chip(onClick = { viewModel.searchByTag(currentTag) }, modifier = Modifier.padding(2.dp)) {
                            Text(modifier = Modifier.padding(8.dp), text = currentTag)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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
                        onDeleteClicked = { itemToDelete ->
                            viewModel.deleteItem(itemToDelete)
                        },
                        onTagClicked = { itemToModify ->
                            backStackHandler.add(Navigation.TagsScreen(itemToModify))
                        },
                        onItemClick = { itemToOpen -> linkManager.openLink(itemToOpen.url) }
                    )
                }
            }

            when (val currentState = state.value) {
                is MainViewModel.MainScreenState.Success -> {
                    data = currentState.linkProperties
                    tags = currentState.tags
                }

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
