package viewmodel.main

import androidx.compose.runtime.mutableStateOf
import datasource.LinkDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import model.LinkProperty
import usecase.GetLinkMetaDataUseCase
import usecase.IsValidUrlUseCase
import viewmodel.ViewModel

class MainViewModel(
    private val getLinkMetadataUseCase: GetLinkMetaDataUseCase,
    private val isValidURLUseCase: IsValidUrlUseCase
) : ViewModel {

    private val viewModelJob = SupervisorJob()
    override val viewModelScope: CoroutineScope =
        CoroutineScope(viewModelJob + Dispatchers.Main.immediate)
    val state = mutableStateOf<MainScreenState>(MainScreenState.Idle)

    sealed class MainScreenState {
        data object Idle : MainScreenState()
        data object Failure : MainScreenState()
        data object InvalidUrl : MainScreenState()
        data class Success(val linkProperty: LinkProperty) : MainScreenState()
    }

    fun validateAndGetMetadata(url: String) {
        viewModelScope.launch {
            if (!isValidURLUseCase(url)) {
                state.value = MainScreenState.InvalidUrl
            } else {
                when (val result = getLinkMetadataUseCase(url)) {
                    // Todo Save To DB
                    LinkDatasource.LinkSearchResult.Failure -> {
                        println("Failure")
                        state.value = MainScreenState.Failure
                    }

                    is LinkDatasource.LinkSearchResult.Success -> {
                        println(result.linkProperty.toString())
                        state.value = MainScreenState.Success(result.linkProperty)
                    }
                }
            }
        }
    }

    override fun dispose() {
        viewModelJob.cancel()
    }
}
