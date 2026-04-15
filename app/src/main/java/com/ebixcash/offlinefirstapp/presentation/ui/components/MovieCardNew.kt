package com.ebixcash.offlinefirstapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ebixcash.offlinefirstapp.data.model.Movie

@Composable
fun MovieCardNew(
    movie: Movie,
    onDelete: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column {
            // Featured Image with Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1a1a1a))
            ) {
                // Thumbnail Image
                AsyncImage(
                    model = movie.thumbnail.ifEmpty { "https://via.placeholder.com/300x200" },
                    contentDescription = movie.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                // Release Date Overlay Card - Bottom Right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = movie.releaseDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        ),
                        fontSize = 12.sp
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Main Title with Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = movie.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Metadata subtitle
                    Text(
                        text = "⭐ ${movie.rating}/10",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF0941E7)
                        )
                    )
                }


                // Description
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF2C2C2C),
                        lineHeight = 18.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons (Like & Share & Delete)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Like button - Conditional Color (Grey if 0, Red if > 0)
                        Row(modifier = Modifier.padding(end = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = onLike, modifier = Modifier.width(36.dp).height(36.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Like",
                                    tint = if (movie.likes == 0) Color(0xFFCCCCCC) else Color(0xFFE91E63),
                                    modifier = Modifier.width(24.dp)
                                )
                            }
                            Text(
                                text = movie.likes.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF888888),
                            )
                        }
                    }

                    // Delete button - Right End
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEE5A52),
                            modifier = Modifier.width(24.dp)
                        )
                    }
                }
            }
        }
    }
}
