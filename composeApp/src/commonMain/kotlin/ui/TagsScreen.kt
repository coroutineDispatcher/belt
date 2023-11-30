package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import model.LinkProperty
import viewmodel.tags.TagsViewModel

@Composable
fun TagsScreen(linkToModify: LinkProperty, onNavigateToMainScreen: () -> Unit) {
    val viewModel = remember { BeltAppDI.tagsViewModel() }
    val tagToSearchQuery = remember { mutableStateOf("") }
    val state = viewModel.state.collectAsState()
    val data = remember { mutableStateOf(listOf<String>()) }

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = tagToSearchQuery.value,
                    onValueChange = {
                        tagToSearchQuery.value = it
                        viewModel.search(tagToSearchQuery.value)
                    },
                    placeholder = {
                        Text(text = "Search or add a new tag")
                    },
                    modifier = Modifier.height(50.dp).fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (!data.value.contains(tagToSearchQuery.value) &&
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
                                    linkToModify,
                                    tagToSearchQuery.value,
                                    then = { onNavigateToMainScreen() }
                                )
                            }
                        )
                    }
                }

                items(data.value) { tag ->
                    TagListItem(
                        textToDisplay = tag,
                        showRemoveTagButton = linkToModify.tags.contains(tag),
                        onListItemClicked = {
                            if (tag.isEmpty()) return@TagListItem
                            viewModel.addTagToItem(
                                linkToModify,
                                tag,
                                then = { onNavigateToMainScreen() }
                            )
                        },
                        onRemoveTag = { viewModel.removeTagFromLinkProperty(linkToModify, tag) }
                    )
                }
            }
        }

        when (val currentState = state.value) {
            TagsViewModel.TagsState.Finish -> onNavigateToMainScreen()
            TagsViewModel.TagsState.Idle -> Unit
            is TagsViewModel.TagsState.Success -> data.value = currentState.tags
        }
    }
}
