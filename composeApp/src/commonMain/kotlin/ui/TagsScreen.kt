package ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import iOS
import model.LinkProperty
import platform
import viewmodel.tags.TagsViewModel

@Composable
fun TagsScreen(linkToModify: LinkProperty, onNavigateToMainScreen: () -> Unit) {
    val viewModel = remember { BeltAppDI.tagsViewModel(linkToModify) }
    val tagToSearchQuery = remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState()
    val dataTags = remember { mutableStateOf(listOf<String>()) }
    val dataCurrentLinkProperty = remember { mutableStateOf(linkToModify) }
    val interactionSource = remember { MutableInteractionSource() }

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }

    key(tagToSearchQuery.value) {
        viewModel.search(tagToSearchQuery.value)
    }

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (platform() == iOS) {
                    IconButton(
                        onClick = { onNavigateToMainScreen() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
                Box(modifier = Modifier.padding(8.dp)) {
                    OutlinedTextField(
                        value = tagToSearchQuery.value,
                        onValueChange = {
                            tagToSearchQuery.value = it
                        },
                        placeholder = {
                            Text(text = "Search or add a new tag")
                        },
                        modifier = Modifier.height(56.dp).fillMaxWidth(),
                        trailingIcon = {
                            if (tagToSearchQuery.value.isNotEmpty()) {
                                IconButton(onClick = { tagToSearchQuery.value = "" }) {
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
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (!dataTags.value.contains(tagToSearchQuery.value) &&
                    tagToSearchQuery.value.isNotEmpty()
                ) {
                    item {
                        TagListItem(
                            textToDisplay = "Add new tag: ${tagToSearchQuery.value}",
                            showRemoveTagButton = false,
                            onRemoveTag = { Unit },
                            onListItemClicked = {
                                if (tagToSearchQuery.value.isEmpty()) return@TagListItem
                                viewModel.addTagToItem(
                                    dataCurrentLinkProperty.value,
                                    tagToSearchQuery.value,
                                    then = { onNavigateToMainScreen() }
                                )
                            }
                        )
                    }
                }

                items(dataTags.value) { tag ->
                    TagListItem(
                        textToDisplay = tag,
                        showRemoveTagButton = dataCurrentLinkProperty.value.tags.contains(tag),
                        onListItemClicked = {
                            if (tag.isEmpty()) return@TagListItem
                            viewModel.addTagToItem(
                                dataCurrentLinkProperty.value,
                                tag,
                                then = { onNavigateToMainScreen() }
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
            TagsViewModel.TagsState.Finish -> onNavigateToMainScreen()
            TagsViewModel.TagsState.Idle -> Unit
            is TagsViewModel.TagsState.Success -> {
                dataTags.value = currentState.tags
                dataCurrentLinkProperty.value = currentState.linkProperty
            }
        }
    }
}
