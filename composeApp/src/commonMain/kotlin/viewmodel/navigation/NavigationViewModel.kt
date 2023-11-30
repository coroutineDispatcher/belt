package viewmodel.navigation

import Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import viewmodel.ViewModel

class NavigationViewModel : ViewModel {
    override val viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _navigationState = MutableStateFlow<Navigation>(Navigation.MainScreen)
    val navigationState = _navigationState.asStateFlow()

    fun navigateTo(navigation: Navigation) {
        viewModelScope.launch { _navigationState.emit(navigation) }
    }

    override fun dispose() {
        viewModelScope.cancel()
    }

    override fun backToIdle() = Unit
}
