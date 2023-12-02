package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource
import model.LinkProperty
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
@Composable
fun LinkPropertyListItem(
    item: LinkProperty,
    onFavoriteClick: (LinkProperty) -> Unit,
    onShareClick: (LinkProperty) -> Unit,
    onDeleteClicked: (LinkProperty) -> Unit,
    onTagClicked: (LinkProperty) -> Unit,
    onItemClick: (LinkProperty) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        elevation = 2.dp,
        modifier = Modifier.padding(8.dp).clickable { onItemClick(item) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier.weight(2f).padding(8.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.h6,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = item.url,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier.weight(1f).padding(8.dp).clip(RoundedCornerShape(16.dp))
                ) {
                    when (val resource = asyncPainterResource(item.image.orEmpty())) {
                        is Resource.Loading -> {
                            Text("Loading...")
                        }

                        is Resource.Success -> {
                            Image(
                                painter = resource.value,
                                contentDescription = "link image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                        }

                        is Resource.Failure -> {
                            // TODO Failure image
                            println(resource.exception)
                        }
                    }
                }
            }

            LazyRow(Modifier.padding(16.dp)) {
                items(item.tags) { tag ->
                    Chip(
                        onClick = { Unit },
                        modifier = Modifier.wrapContentSize().padding(2.dp)
                    ) {
                        Text(tag)
                    }
                }
            }

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.SpaceAround
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
                    onClick = { onTagClicked(item) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(painter = painterResource("tag.png"), "Tag")
                }

                IconButton(
                    onClick = { onDeleteClicked(item) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(Icons.Filled.Delete, "Delete")
                }
            }
        }
    }
}
