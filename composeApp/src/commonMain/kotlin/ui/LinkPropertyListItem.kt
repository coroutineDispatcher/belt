package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
import model.LinkProperty

@Composable
fun LinkPropertyListItem(
    item: LinkProperty,
    onFavoriteClick: (LinkProperty) -> Unit,
    onShareClick: (LinkProperty) -> Unit,
    onDeleteClick: (LinkProperty) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        elevation = 2.dp,
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.url,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().align(Alignment.CenterVertically)
            ) {
                when (val resource = asyncPainterResource(item.image.orEmpty())) {
                    is Resource.Loading -> {
                        Text("Loading...")
                    }

                    is Resource.Success -> {
                        val painter: Painter = resource.value
                        Image(
                            painter,
                            contentDescription = "link image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    is Resource.Failure -> {
                        println(resource.exception)
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    IconButton(
                        onClick = { onFavoriteClick(item) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        val icon =
                            if (item.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

                        Icon(icon, "Favorite")
                    }

                    IconButton(
                        onClick = { onShareClick(item) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Share, "Share")
                    }

                    IconButton(
                        onClick = { onDeleteClick(item) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Delete, "Delete")
                    }
                }
            }
        }
    }
}
