package navigation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import model.LinkProperty
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BackStackHandlerTest {
    private val sut = BackStackHandler(initialScreen = Navigation.MainScreen)

    @Test
    fun `MainScreen is the initial state`() = runTest {
        sut.navigation.test {
            val defaultScreen = awaitItem()
            println(defaultScreen)
            assertTrue { defaultScreen == Navigation.MainScreen }
        }
    }

    @Test
    fun `adding a new screen is reflected on the back stack`() = runTest {
        sut.navigation.test {
            awaitItem() // Default main screen or initial screen
            sut.add(Navigation.AddNewLinkScreen)
            val newAddedScreen = awaitItem()
            assertTrue { newAddedScreen == Navigation.AddNewLinkScreen }
        }
    }

    @Test
    fun `popping the backstack takes me to the previous screen`() = runTest {
        sut.navigation.test {
            awaitItem()
            sut.add(Navigation.TagsScreen(LinkProperty()))
            awaitItem()
            sut.add(Navigation.AddNewLinkScreen)
            awaitItem()
            sut.popBackStack()
            val correctScreen = awaitItem()
            assertTrue { correctScreen is Navigation.TagsScreen }
        }
    }

    @Test
    fun `popping the backstack twice takes me to the previous screen`() = runTest {
        sut.navigation.test {
            awaitItem()
            sut.add(Navigation.TagsScreen(LinkProperty()))
            awaitItem()
            sut.add(Navigation.AddNewLinkScreen)
            awaitItem()
            sut.popBackStack()
            awaitItem()
            sut.popBackStack()
            val correctScreen = awaitItem()
            assertTrue { correctScreen is Navigation.MainScreen }
        }
    }

    @Test
    fun `no matter how many items there are in the backstack pop to last should clear all except the initial`() =
        runTest {
            sut.navigation.test {
                awaitItem()
                sut.add(Navigation.TagsScreen(LinkProperty()))
                awaitItem()
                sut.add(Navigation.AddNewLinkScreen)
                awaitItem()
                sut.add(Navigation.TagsScreen(LinkProperty()))
                awaitItem()
                sut.add(Navigation.AddNewLinkScreen)
                awaitItem()

                sut.popToLast()

                val correctScreen = awaitItem()
                assertTrue { correctScreen is Navigation.MainScreen }
            }
        }

    @Test
    fun `if the screen is in the initial state the backstack handler should reflect that`() {
        sut.popToLast()

        assertTrue { sut.initialState() }
    }

    @Test
    fun `if nothing is in the backstack the app should finish ie the state is null`() = runTest {
        sut.navigation.test {
            awaitItem()
            sut.clearAndFinish()
            val latestState = awaitItem()
            assertNull(latestState)
            assertFalse { sut.initialState() }
        }
    }
}
