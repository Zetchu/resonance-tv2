package edu.rit.dk9612.resonancetv.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share // NEW IMPORT
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.*
import edu.rit.dk9612.resonancetv.SanctuaryViewModel
import edu.rit.dk9612.resonancetv.VideoItem
import edu.rit.dk9612.resonancetv.data.repository.FirestoreRepository // NEW IMPORT

@Composable
fun MainAppScreen(
    sanctuaryViewModel: SanctuaryViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf("Home") }
    var selectedVideo by remember { mutableStateOf<VideoItem?>(null) }

    var isHeroPlayerOpen by remember { mutableStateOf(false) }
    val heroMockVideo = VideoItem("hero_id", "Sónar 2026", "Live", "LIVE", "", "", 5)

    val savedVideosEntities by sanctuaryViewModel.savedVideos.observeAsState(emptyList())
    val sanctuaryList = savedVideosEntities.map { entity ->
        VideoItem(
            id = entity.id,
            title = entity.title,
            subtitle = entity.subtitle,
            duration = entity.duration,
            thumbnailUrl = entity.thumbnailUrl
        )
    }

    if (isHeroPlayerOpen) {
        PlayerScreen(
            video = heroMockVideo,
            onNavigateBack = { isHeroPlayerOpen = false }
        )
    }
    else if (selectedVideo != null) {
        // 1. Convert the static selectedVideo into a LIVE state!
        val liveVideo by FirestoreRepository.getLiveVideoFlow(selectedVideo!!)
            .collectAsState(initial = selectedVideo!!)

        val isSaved = sanctuaryList.any { it.id == liveVideo.id }

        DetailsScreen(
            video = liveVideo, // 2. Pass the LIVE video here instead of selectedVideo!!
            isSaved = isSaved,
            onNavigateBack = { selectedVideo = null },
            onToggleSave = { video ->
                if (isSaved) sanctuaryViewModel.removeVideo(video)
                else sanctuaryViewModel.addVideo(video)
            },
            onPlayClicked = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + liveVideo.id))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + liveVideo.id))
                    context.startActivity(webIntent)
                }
            },
            onLikeToggle = { video ->
                // This triggers the database transaction.
                // As soon as Firebase updates, getLiveVideoFlow catches it and updates the UI instantly!
                FirestoreRepository.toggleLike(video)
            }
        )
    }else {
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // NEW: The Community Vault Tab!
                    NavigationDrawerItem(
                        selected = currentScreen == "Community",
                        onClick = { currentScreen = "Community" },
                        leadingContent = { Icon(Icons.Default.Share, contentDescription = "Community") }
                    ) { Text("Community") }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                if (currentScreen == "Home") {
                    HomeScreen(
                        onVideoClick = { video: VideoItem -> selectedVideo = video },
                        onHeroClick = { isHeroPlayerOpen = true }
                    )
                } else if (currentScreen == "Library") {
                    LibraryScreen(
                        savedVideos = sanctuaryList,
                        onVideoClick = { video: VideoItem -> selectedVideo = video }
                    )
                } else if (currentScreen == "Community") {
                    // Pull the live Firebase data straight into the state
                    val communityVideos by FirestoreRepository.getCommunityVideosFlow().collectAsState(initial = emptyList())

                    CommunityVaultScreen(
                        communityVideos = communityVideos,
                        savedVideos = sanctuaryList,
                        onVideoClick = { video: VideoItem -> selectedVideo = video },
                        onToggleSave = { video ->
                            val isSaved = sanctuaryList.any { it.id == video.id }
                            if (isSaved) sanctuaryViewModel.removeVideo(video)
                            else sanctuaryViewModel.addVideo(video)
                        }
                    )
                }
            }
        }
    }
}