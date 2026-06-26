package edu.rit.dk9612.resonancetv.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import edu.rit.dk9612.resonancetv.VideoItem
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
            text = "Community Vault",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (communityVideos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No sets shared yet. Be the first to drop a mix!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                items(communityVideos) { video ->
                    val isSaved = savedVideos.any { it.id == video.id }
                    CommunityVideoRow(
                        video = video,
                        isSaved = isSaved,
                        onVideoClick = { onVideoClick(video) },
                        onToggleSave = { onToggleSave(video) },
                        onLikeClick = { FirestoreRepository.likeVideo(video.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityVideoRow(
    video: VideoItem,
    isSaved: Boolean,
    onVideoClick: () -> Unit,
    onToggleSave: () -> Unit,
    onLikeClick: () -> Unit
) {
    // 1. Grab the context HERE so the Toasts can use it!
    val context = LocalContext.current

    // 2. The main container is now a simple, unfocusable Row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 3. The Card is only for the Image and Text (takes up the left side)
        Card(
            onClick = onVideoClick,
            modifier = Modifier
                .weight(1f) // Takes up all remaining space left of the buttons
                .fillMaxHeight(),
            colors = CardDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            scale = CardDefaults.scale(focusedScale = 1.02f)
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = video.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }

        // 4. The Action Buttons sit OUTSIDE the Card, making them individually focusable!
        Row(
            modifier = Modifier.padding(start = 24.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like Button & Counter
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    onLikeClick()
                    // TOAST FEEDBACK
                    Toast.makeText(context, "Liked Set!", Toast.LENGTH_SHORT).show()
                }){
                    Icon(Icons.Default.ThumbUp, contentDescription = "Like")
                }
                Text(
                    text = "${video.likes}",
                    style = MaterialTheme.typography.titleMedium, // Made slightly bigger for TV
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Save to Sanctuary Button
            IconButton(onClick = {
                onToggleSave()
                // TOAST FEEDBACK
                val msg = if (isSaved) "Removed from Sanctuary" else "Added to Sanctuary"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save"
                )
            }
        }
    }
}