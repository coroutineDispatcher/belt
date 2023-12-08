package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TagListItem(
    textToDisplay: String,
    showRemoveTagButton: Boolean = false,
    showDeleteTagButton: Boolean = false,
    onListItemClicked: () -> Unit,
    onRemoveTag: () -> Unit,
    onDeleteTag: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onListItemClicked()
            }
    ) {
        Text(
            text = textToDisplay,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Light,
            modifier =
            Modifier.fillMaxWidth().padding(16.dp).weight(3f)
        )

        if (showRemoveTagButton) {
            IconButton(
                onClick = { onRemoveTag() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.Clear,
                    "Remove tag"
                )
            }
        }

        if (showDeleteTagButton) {
            IconButton(
                onClick = { onDeleteTag(textToDisplay) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Filled.Delete,
                    Icons.Filled.Delete.name
                )
            }
        }
    }
}
