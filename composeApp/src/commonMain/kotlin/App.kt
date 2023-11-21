import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import datasource.LinkDatasource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import repository.LinksRepository
import usecase.GetLinkMetaDataUseCase
import usecase.IsValidUrlUseCase
import viewmodel.main.MainViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    // DI
    val linkDatasource = LinkDatasource()
    val repository = LinksRepository(linkDatasource)
    val viewModel = MainViewModel(
        GetLinkMetaDataUseCase(repository),
        IsValidUrlUseCase(repository)
    )
    DisposableEffect(Unit) {
        onDispose { viewModel.dispose() }
    }
    MaterialTheme {
        var url by remember { mutableStateOf("") }
        val state = remember { viewModel.state }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = url,
                onValueChange = { value -> url = value }
            )
            Button(onClick = {
                viewModel.validateAndGetMetadata(url)
            }) {
                Text("Validate")
            }
            when (val currentState = state.value) {
                MainViewModel.MainScreenState.Failure -> println("Failed")
                MainViewModel.MainScreenState.Idle -> Unit
                MainViewModel.MainScreenState.InvalidUrl -> println("Invalid URL")
                is MainViewModel.MainScreenState.Success -> {
                    AnimatedVisibility(true) {
                        Text(currentState.linkProperty.title, color = Color.Black)
                    }
                }
            }
        }
    }
}