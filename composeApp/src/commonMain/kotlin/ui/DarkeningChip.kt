package ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DarkeningChip(currentTag: String, onFirstClick: () -> Unit, onSecondClick: () -> Unit) {
    val clicked = remember { mutableStateOf(false) }

    FilterChip(
        onClick = {
            when {
                !clicked.value -> onFirstClick()
                else -> onSecondClick()
            }
            clicked.value = !clicked.value
        },
        modifier = Modifier.padding(2.dp),
        selected = clicked.value
    ) {
        Text(modifier = Modifier.padding(8.dp), text = currentTag)
    }
}
