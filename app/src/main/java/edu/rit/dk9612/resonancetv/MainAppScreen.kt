package edu.rit.dk9612.resonancetv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.*

@Composable
fun MainAppScreen(
    // Inject the ViewModel here. Compose will automatically create or retrieve it!
    sanctuaryViewModel: SanctuaryViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf("Home") }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    // 1. Observe the database! Whenever Room updates, this list updates automatically.
    val savedVideosEntities by sanctuaryViewModel.savedVideos.observeAsState(emptyList())

    // 2. Map the database entities back into the VideoItems our UI expects
    val sanctuaryList = savedVideosEntities.map { entity ->
        VideoItem(entity.id, entity.title, entity.subtitle, entity.duration)
    }

    if (selectedVideo != null) {
        // Check if the current video's ID is in our database list
        val isSaved = sanctuaryList.any { it.id == selectedVideo!!.id }

        DetailsScreen(
            video = selectedVideo!!,
            isSaved = isSaved,
            onNavigateBack = { selectedVideo = null },
            onToggleSave = { video ->
                // 3. Trigger the ViewModel to actually save or delete from Room
                if (isSaved) {
                    sanctuaryViewModel.removeVideo(video)
                } else {
                    sanctuaryViewModel.addVideo(video)
                }
            }
        )
    } else {
        NavigationDrawer(
            drawerContent = { drawerValue ->
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    NavigationDrawerItem(
                        selected = currentScreen == "Home",
                        onClick = { currentScreen = "Home" },
                        leadingContent = { Icon(Icons.Default.Home, contentDescription = "Home") }
                    ) { Text("Home") }

                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationDrawerItem(
                        selected = currentScreen == "Library",
                        onClick = { currentScreen = "Library" },
                        leadingContent = { Icon(Icons.Default.Favorite, contentDescription = "Library") }
                    ) { Text("Sanctuary") }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (currentScreen == "Home") {
                    HomeScreen(onVideoClick = { video: VideoItem -> selectedVideo = video })
                } else {
                    // Pass the real database list to your Library screen!
                    LibraryScreen(
                        savedVideos = sanctuaryList,
                        // Added explicit : VideoItem types to fix the infer error
                        onVideoClick = { video: VideoItem -> selectedVideo = video }
                    )
                }
            }
        }
    }
}