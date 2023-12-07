package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BeltSearchBar(
    value: String,
    hint: String,
    onValueChange: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().wrapContentSize().padding(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(value) },
            placeholder = {
                Text(text = hint)
            },
            modifier = Modifier.height(56.dp).fillMaxWidth(),
            maxLines = 1
        )
    }
}
