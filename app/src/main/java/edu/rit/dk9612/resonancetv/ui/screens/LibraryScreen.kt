package edu.rit.dk9612.resonancetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import edu.rit.dk9612.resonancetv.data.model.VideoItem


@Composable
fun LibraryScreen(savedVideos: List<VideoItem>, onVideoClick: (VideoItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 48.dp, start = 32.dp, end = 32.dp)) {
        Text(
            text = "My Sanctuary",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (savedVideos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your sanctuary is empty. Save some sets!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 48.dp)
            ) {
                items(savedVideos) { video ->
                    BasicVideoRow(video = video, onClick = { onVideoClick(video) })
                }
            }
        }
    }
}

@Composable
fun BasicVideoRow(video: VideoItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        colors = CardDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        scale = CardDefaults.scale(focusedScale = 1.02f)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(200.dp).fillMaxHeight()
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = video.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = video.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.padding(end = 24.dp)
            )
        }
    }
}