package navigation

import Navigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackStackHandler(initialScreen: Navigation) {
    private val backStack = mutableListOf<Navigation>()
    private val _currentNav = MutableStateFlow<Navigation?>(current())
    val navigation = _currentNav.asStateFlow()

    init {
        add(initialScreen)
    }

    fun add(navigation: Navigation) {
        backStack.add(navigation)
        update()
    }

    fun popBackStack() {
        if (current() != null) {
            backStack.removeLast()
        }
        update()
    }

    private fun update() {
        _currentNav.tryEmit(current())
    }

    private fun current() = backStack.lastOrNull()

    fun clearAndFinish() {
        backStack.clear()
        update()
    }
}
