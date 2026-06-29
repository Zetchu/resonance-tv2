package edu.rit.dk9612.resonancetv.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.*
import edu.rit.dk9612.resonancetv.SanctuaryViewModel
import edu.rit.dk9612.resonancetv.data.model.VideoItem

import edu.rit.dk9612.resonancetv.data.repository.FirestoreRepository

@Composable
fun MainAppScreen(
    sanctuaryViewModel: SanctuaryViewModel = viewModel()
) {
    val context = LocalContext.current

    // 1. Initialize the Navigation Controller
    val navController = rememberNavController()

    // 2. State to hold the complex VideoItem while we navigate using its ID
    var sharedSelectedVideo by remember { mutableStateOf<VideoItem?>(null) }
    val heroMockVideo = VideoItem("hero_id", "Sónar 2026", "Live", "LIVE", "", "", 5)

    // Keep Sanctuary list observer at the top so all screens can access it
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

    // 3. The NavHost handles all screen routing
    NavHost(navController = navController, startDestination = "home") {

        // --- MAIN TABS (Wrapped in our Drawer layout) ---
        composable("home") {
            MainLayoutWithDrawer(currentRoute = "home", navController = navController) {
                HomeScreen(
                    onVideoClick = { video ->
                        sharedSelectedVideo = video
                        // Pass the ID as route data to satisfy navigation requirements!
                        navController.navigate("details/${video.id}")
                    },
                    onHeroClick = {
                        sharedSelectedVideo = heroMockVideo
                        navController.navigate("player/hero_id")
                    }
                )
            }
        }

        composable("library") {
            MainLayoutWithDrawer(currentRoute = "library", navController = navController) {
                LibraryScreen(
                    savedVideos = sanctuaryList,
                    onVideoClick = { video ->
                        sharedSelectedVideo = video
                        navController.navigate("details/${video.id}")
                    }
                )
            }
        }

        composable("community") {
            MainLayoutWithDrawer(currentRoute = "community", navController = navController) {
                val communityVideos by FirestoreRepository.getCommunityVideosFlow().collectAsState(initial = emptyList())

                CommunityVaultScreen(
                    communityVideos = communityVideos,
                    savedVideos = sanctuaryList,
                    onVideoClick = { video ->
                        sharedSelectedVideo = video
                        navController.navigate("details/${video.id}")
                    },
                    onToggleSave = { video ->
                        val isSaved = sanctuaryList.any { it.id == video.id }
                        if (isSaved) sanctuaryViewModel.removeVideo(video)
                        else sanctuaryViewModel.addVideo(video)
                    }
                )
            }
        }

        // --- SUB SCREENS (No Drawer) ---

        // DETAILS DESTINATION - Grabs the passed videoId argument
        composable("details/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId")

            // Ensure we have data for the ID that was passed
            if (sharedSelectedVideo != null && sharedSelectedVideo?.id == videoId) {

                // Apply our live Firebase listener we built previously
                val liveVideo by FirestoreRepository.getLiveVideoFlow(sharedSelectedVideo!!)
                    .collectAsState(initial = sharedSelectedVideo!!)

                val isSaved = sanctuaryList.any { it.id == liveVideo.id }

                DetailsScreen(
                    video = liveVideo,
                    isSaved = isSaved,
                    onNavigateBack = { navController.popBackStack() }, // True Back Navigation!
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
                    onLikeToggle = { video -> FirestoreRepository.toggleLike(video) }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Video Data Unavailable", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        composable("player/{videoId}") {
            if (sharedSelectedVideo != null) {
                PlayerScreen(
                    video = sharedSelectedVideo!!,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Helper wrapper to enforce the Drawer UI on main tabs
@Composable
fun MainLayoutWithDrawer(
    currentRoute: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    NavigationDrawer(
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                NavigationDrawerItem(
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    leadingContent = { Icon(Icons.Default.Home, contentDescription = "Home") }
                ) { Text("Home") }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    selected = currentRoute == "library",
                    onClick = {
                        navController.navigate("library") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    leadingContent = { Icon(Icons.Default.Favorite, contentDescription = "Library") }
                ) { Text("Sanctuary") }

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    selected = currentRoute == "community",
                    onClick = {
                        navController.navigate("community") {
                            launchSingleTop = true
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = "Community") }
                ) { Text("Community") }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            content()
        }
    }
}