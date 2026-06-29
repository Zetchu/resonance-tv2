package edu.rit.dk9612.resonancetv.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.repository.FirestoreRepository

@Composable
fun CommunityVaultScreen(
    communityVideos: List<VideoItem>,
    savedVideos: List<VideoItem>,
    onVideoClick: (VideoItem) -> Unit,
    onToggleSave: (VideoItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 32.dp, end = 32.dp)
    ) {
        Text(
            text = "VIBE CHECK: COMMUNITY",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Connecting the pulse of the underground. See what the\nsanctuary is playing right now.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Shared by Community",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (communityVideos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No sets shared yet. Be the first to drop a mix!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                items(communityVideos) { video ->
                    val isSaved = savedVideos.any { it.id == video.id }
                    CommunityCard(
                        video = video,
                        isSaved = isSaved,
                        onVideoClick = { onVideoClick(video) },
                        onToggleSave = { onToggleSave(video) },
                        onLikeClick = { FirestoreRepository.toggleLike(video) } // Links directly to your transactional Firebase setup
                    )
                }
            }
        }
    }
}
@Composable
fun CommunityCard(
    video: VideoItem,
    isSaved: Boolean,
    onVideoClick: () -> Unit,
    onToggleSave: () -> Unit,
    onLikeClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(bottom = 16.dp)
    ) {
        Card(
            onClick = onVideoClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.colors(containerColor = Color.Transparent),
            shape = CardDefaults.shape(shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)),
            scale = CardDefaults.scale(focusedScale = 1.02f)
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = video.title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    onLikeClick()
                    val msg = if (video.isLikedByMe) "Unliked Set" else "Liked Set!"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.colors(
                    containerColor = if (video.isLikedByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,

                    contentColor = if (video.isLikedByMe) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,

                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedContentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = if (video.isLikedByMe) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like"
                )
                Spacer(modifier = Modifier.width(8.dp))

                val countText = if (video.likes >= 1000) "${String.format("%.1f", video.likes / 1000.0)}k" else "${video.likes}"

                Text(
                    text = "$countText Likes",
                    fontWeight = if (video.isLikedByMe) FontWeight.Bold else FontWeight.Normal
                )
            }

            OutlinedButton(
                onClick = {
                    onToggleSave()
                    val msg = if (isSaved) "Removed from Sanctuary" else "Added to Sanctuary"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedButtonDefaults.colors(
                    containerColor = if (isSaved) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
                )
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isSaved) "Saved" else "Save")
            }
        }
    }
}