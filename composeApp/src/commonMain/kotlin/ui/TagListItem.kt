package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TagListItem(
    textToDisplay: String,
    showRemoveTagButton: Boolean = false,
    onListItemClicked: () -> Unit,
    onRemoveTag: () -> Unit
) {
    val showRemoveTagButton = remember { showRemoveTagButton }

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(
            text = textToDisplay,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    onListItemClicked()
                }
                .wrapContentSize()
                .padding(16.dp)
        )

        println("Should show remove tag button: $showRemoveTagButton")

        if (showRemoveTagButton) {
            IconButton(
                modifier = Modifier.padding(start = 16.dp),
                onClick = {
                    onRemoveTag()
                }
            ) {
                Icon(
                    Icons.Filled.Clear,
                    "Remove tag"
                )
            }
        }
    }
}
