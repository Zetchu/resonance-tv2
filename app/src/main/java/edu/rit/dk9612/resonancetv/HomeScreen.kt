package edu.rit.dk9612.resonancetv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*

@Composable
fun HomeScreen(onVideoClick: (VideoItem) -> Unit) {
    // LazyColumn allows vertical scrolling via the D-pad
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Safe area padding
    ) {
        // 1. The Hero Banner at the top
        item {
            HeroBanner()
        }

        // 2. The Horizontal Carousels
        items(MockData.homeCategories) { category ->
            // UPDATE 2: Pass the lambda down to the Carousel
            VideoCarousel(category = category, onVideoClick = onVideoClick)
        }
    }
}

@Composable
fun HeroBanner() {
    // This represents the featured "Sónar Festival 2026" banner
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
        ) {
            Text(
                text = "LIVE NOW • Gear: 3x CDJ-3000s, V10 Mixer",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sónar Festival 2026\nMainstage Experience",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun VideoCarousel(category: VideoCategory, onVideoClick: (VideoItem) -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        // Carousel Title
        Text(
            text = category.categoryName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
        )

        // The Horizontal Scrolling Row
        LazyRow(
            contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(category.videos) { video ->
                // UPDATE 3: Pass the lambda down to the Card
                VideoCard(video = video, onVideoClick = onVideoClick)
            }
        }
    }
}

@Composable
fun VideoCard(video: VideoItem, onVideoClick: (VideoItem) -> Unit) {
    // The TV Material Card automatically handles D-pad focus scaling!
    Card(
        // UPDATE 4: Actually trigger the click with the current video!
        onClick = { onVideoClick(video) },
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = CardDefaults.shape(shape = RoundedCornerShape(8.dp)),
        // Apply the 3px Primary Blue border and neon glow on focus
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            )
        ),
        glow = CardDefaults.glow(
            focusedGlow = Glow(
                elevationColor = MaterialTheme.colorScheme.primary,
                elevation = 20.dp
            )
        ),
        scale = CardDefaults.scale(focusedScale = 1.05f) // 5% focus scale from design.md
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Mock Thumbnail Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A1A1A))
            )

            // Duration Pill in bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = video.duration,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Video Info (Title & Subtitle) overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = video.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = video.subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}