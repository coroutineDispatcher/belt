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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import model.LinkSearchProperty
import navigation.BackStackHandler
import navigation.Navigation
import viewmodel.MainScreenState

@Composable
fun MainScreen(
    backStackHandler: BackStackHandler
) {
    val viewModel = remember { BeltAppDI.mainViewModel() }
    val linkManager = remember { linkManager }
    var data by remember { mutableStateOf(emptyList<LinkProperty>()) }
    var searchQuery by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    var dataTags by remember { mutableStateOf(listOf<String>()) }
    val dataLinkSearchProperties by remember { mutableStateOf(LinkSearchProperty.entries - LinkSearchProperty.None) }
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
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(16.dp).align(Alignment.Center).fillMaxWidth(),
                    onClick = { backStackHandler.add(Navigation.AddNewLinkScreen) },
                    text = { Text("Add new item") },
                    icon = { Icon(Icons.Filled.Add, Icons.Filled.Add.name) }
                )
            }
        },
        topBar = {
            Column(verticalArrangement = Arrangement.SpaceAround) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
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
                            interactionSource = interactionSource,
                            maxLines = 1
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 2.dp, bottom = 2.dp, start = 4.dp, end = 4.dp)
                ) {
                    items(dataLinkSearchProperties) { linkSearchProperty ->
                        DarkeningChip(
                            currentTag = "${linkSearchProperty.emoji} ${linkSearchProperty.name}",
                            onFirstClick = { viewModel.searchByLinkProperty(linkSearchProperty) },
                            onSecondClick = { viewModel.clearSearchProperty() }
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 2.dp, bottom = 2.dp, start = 4.dp, end = 4.dp)
                ) {
                    items(dataTags) { currentTag ->
                        DarkeningChip(
                            currentTag = currentTag,
                            onFirstClick = { viewModel.searchByTag(currentTag) },
                            onSecondClick = { viewModel.removeTagFromFilter(currentTag) }
                        )
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
                is MainScreenState.Success -> {
                    data = currentState.linkProperties
                    dataTags = currentState.tags
                }

                MainScreenState.Idle -> Unit
                MainScreenState.Empty -> Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxSize()
                ) {
                    Text("Implement empty state", color = Color.Black)
                }
            }
        }
    }
}
