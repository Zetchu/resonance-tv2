package edu.rit.dk9612.resonancetv

import android.content.Intent // Add this
import android.net.Uri // Add this
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Add this
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.*

@Composable
fun MainAppScreen(
    sanctuaryViewModel: SanctuaryViewModel = viewModel()
) {
    val context = LocalContext.current // Need this to launch Intents
    var currentScreen by remember { mutableStateOf("Home") }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    // NEW: Track if the Hero player is open
    var isHeroPlayerOpen by remember { mutableStateOf(false) }
    val heroMockVideo = VideoItem("hero_id", "Sónar 2026", "Live", "LIVE", "", "")

    val savedVideosEntities by sanctuaryViewModel.savedVideos.observeAsState(emptyList())
    val sanctuaryList = savedVideosEntities.map { entity ->
        VideoItem(entity.id, entity.title, entity.subtitle, entity.duration)
    }

    // 1. Check if Hero Player should be open
    if (isHeroPlayerOpen) {
        PlayerScreen(
            video = heroMockVideo,
            onNavigateBack = { isHeroPlayerOpen = false }
        )
    }
    // 2. Check if a normal video is selected
    else if (selectedVideo != null) {
        val isSaved = sanctuaryList.any { it.id == selectedVideo!!.id }

        DetailsScreen(
            video = selectedVideo!!,
            isSaved = isSaved,
            onNavigateBack = { selectedVideo = null },
            onToggleSave = { video ->
                if (isSaved) sanctuaryViewModel.removeVideo(video)
                else sanctuaryViewModel.addVideo(video)
            },
            onPlayClicked = {
                // LAUNCH THE NATIVE YOUTUBE APP INTENT!
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + selectedVideo!!.id))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to web browser if the YouTube app isn't installed on the device
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + selectedVideo!!.id))
                    context.startActivity(webIntent)
                }
            }
        )
    } else {
        NavigationDrawer(
            drawerContent = { drawerValue ->
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxHeight().padding(16.dp),
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
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                if (currentScreen == "Home") {
                    HomeScreen(
                        onVideoClick = { video: VideoItem -> selectedVideo = video },
                        onHeroClick = { isHeroPlayerOpen = true } // TRIGGER THE CUSTOM PLAYER
                    )
                } else {
                    LibraryScreen(
                        savedVideos = sanctuaryList,
                        onVideoClick = { video: VideoItem -> selectedVideo = video }
                    )
                }
            }
        }
    }
}