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
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import model.LinkProperty
import navigation.BackStackHandler
import viewmodel.TagsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TagsScreen(linkToModify: LinkProperty, backStackHandler: BackStackHandler) {
    val viewModel = remember { BeltAppDI.tagsViewModel(linkToModify) }
    var tagToSearchQuery by remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState()
    var dataTags by remember { mutableStateOf(listOf<String>()) }
    val dataCurrentLinkProperty = remember { mutableStateOf(linkToModify) }
    val interactionSource = remember { MutableInteractionSource() }

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }

    key(tagToSearchQuery) {
        viewModel.search(tagToSearchQuery)
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            Column(verticalArrangement = Arrangement.SpaceAround) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = tagToSearchQuery,
                            onValueChange = {
                                tagToSearchQuery = it
                            },
                            placeholder = {
                                Text(text = "Search or add a new tag")
                            },
                            modifier = Modifier.height(56.dp).fillMaxWidth(),
                            trailingIcon = {
                                if (tagToSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { tagToSearchQuery = "" }) {
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
                    items(dataCurrentLinkProperty.value.tags) { currentTag ->
                        Chip(onClick = { Unit }, modifier = Modifier.padding(2.dp)) {
                            Text(modifier = Modifier.padding(8.dp), text = currentTag)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (!dataTags.contains(tagToSearchQuery) &&
                    tagToSearchQuery.isNotEmpty()
                ) {
                    item {
                        TagListItem(
                            textToDisplay = "Add new tag: $tagToSearchQuery",
                            showRemoveTagButton = false,
                            onRemoveTag = { Unit },
                            onListItemClicked = {
                                if (tagToSearchQuery.isEmpty()) return@TagListItem
                                viewModel.addTagToItem(
                                    dataCurrentLinkProperty.value,
                                    tagToSearchQuery,
                                    then = { backStackHandler.popToLast() }
                                )
                            }
                        )
                    }
                }

                items(dataTags) { tag ->
                    TagListItem(
                        textToDisplay = tag,
                        showRemoveTagButton = dataCurrentLinkProperty.value.tags.contains(tag),
                        onListItemClicked = {
                            if (tag.isEmpty()) return@TagListItem
                            viewModel.addTagToItem(
                                dataCurrentLinkProperty.value,
                                tag,
                                then = { backStackHandler.popToLast() }
                            )
                        },
                        onRemoveTag = {
                            viewModel.removeTagFromLinkProperty(
                                dataCurrentLinkProperty.value,
                                tag
                            )
                        }
                    )
                }
            }
        }
        when (val currentState = state.value) {
            TagsState.Finish -> backStackHandler.popToLast()
            TagsState.Idle -> Unit
            is TagsState.Success -> {
                dataTags = currentState.tags
                dataCurrentLinkProperty.value = currentState.linkProperty
            }
        }
    }
}
