import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.BeltAppDI
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ui.BeltDialog
import ui.LinkPropertyListItem
import viewmodel.main.MainViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val viewModel = remember { BeltAppDI.mainViewModel }

    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }
    MaterialTheme {
        var url by remember { mutableStateOf("") }
        val state = remember { viewModel.state }
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f).height(50.dp)) {
                    TextField(
                        value = url,
                        onValueChange = { url = it },
                        placeholder = {
                            Text(text = "Place your link here")
                        }
                    )
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = { viewModel.validateAndGetMetadata(url) },
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "Add")
                }
            }
            when (val currentState = state.value) {
                is MainViewModel.MainScreenState.Success -> {
                    AnimatedVisibility(true) {
                        LazyColumn {
                            items(currentState.linkProperty) { item ->
                                LinkPropertyListItem(
                                    item,
                                    onFavoriteClick = {
                                        viewModel.toggleFavorite(item)
                                    },
                                    onShareClick = {
                                        // TODO
                                    },
                                    onDeleteClick = { item ->
                                        viewModel.deleteItem(item)
                                    }
                                )
                            }
                        }
                    }
                }

                MainViewModel.MainScreenState.Failure -> {
                    BeltDialog(
                        title = "Something went wrong",
                        content = "Something went wrong. Please try again.",
                        onDismiss = {
                        },
                        onConfirm = {
                        }
                    )
                }

                MainViewModel.MainScreenState.Idle -> Unit
                MainViewModel.MainScreenState.InvalidUrl -> {
                    BeltDialog(
                        title = "Invalid URL",
                        content = "The submitted URL is invalid. Please try another one.",
                        onDismiss = {
                        },
                        onConfirm = {
                        }
                    )
                }
            }
        }
    }
}
