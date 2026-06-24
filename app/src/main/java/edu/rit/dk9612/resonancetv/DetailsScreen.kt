package edu.rit.dk9612.resonancetv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun DetailsScreen(
    video: VideoItem,
    isSaved: Boolean,
    onNavigateBack: () -> Unit,
    onToggleSave: (VideoItem) -> Unit
) {
    // Intercepts the physical "Back" button on the TV remote
    BackHandler(onBack = onNavigateBack)

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Immersive Background Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
        )

        // 2. Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 1500f
                    )
                )
        )

        // 3. On-Screen Back Button (Top Left)
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(32.dp),
            colors = IconButtonDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedContentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go Back"
            )
        }

        // 4. Content Layer (Text and Buttons)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f) // Content only takes up the left half of the screen
                .padding(start = 56.dp, top = 56.dp, bottom = 32.dp, end = 32.dp) // Reduced bottom padding slightly
                .verticalScroll(rememberScrollState()), // ADD THIS LINE!
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "INDUSTRIAL TECHNO • 135 BPM",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = video.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${video.subtitle} • ${video.duration}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recorded live from the underground vaults. A relentless, driving techno journey featuring heavy bass kicks, acid synths, and classic 909 percussion. \n\nGear: 4x CDJ-3000, Allen & Heath Xone:96, RMX-1000.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons Row
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Play Button
                Button(
                    onClick = { /* TODO: Launch Visualizer/Player */ },
                    colors = ButtonDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primary,
                        focusedContentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Set", style = MaterialTheme.typography.titleMedium)
                }

                // Save to Sanctuary Button
                OutlinedButton(
                    onClick = { onToggleSave(video) },
                    colors = OutlinedButtonDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContentColor = MaterialTheme.colorScheme.primary,
                        containerColor = if (isSaved) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = OutlinedButtonDefaults.border(
                        border = Border(BorderStroke(2.dp, MaterialTheme.colorScheme.onSurfaceVariant)),
                        focusedBorder = Border(BorderStroke(3.dp, MaterialTheme.colorScheme.primary))
                    ),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaved) "Remove from Sanctuary" else "Add to Sanctuary",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}