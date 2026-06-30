package edu.rit.dk9612.resonancetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.*
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import androidx.compose.ui.graphics.Color
import edu.rit.dk9612.resonancetv.ui.viewmodels.DiscoverViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onVideoClick: (VideoItem) -> Unit,
    viewModel: DiscoverViewModel = viewModel()
) {
    val results by viewModel.searchResults.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text(
            text = "Explore Genres",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            items(viewModel.genres) { genre ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.searchByGenre(genre) },
                    colors = FilterChipDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,

                        focusedContainerColor = MaterialTheme.colorScheme.primary,
                        focusedContentColor = Color.Black,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedContentColor = Color.Black
                    )
                ) {
                    Text(genre)
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(results) { video ->
                VideoCard(video = video, onVideoClick = onVideoClick)
            }
        }
    }
}