package edu.rit.dk9612.resonancetv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun PlayerScreen(
    video: VideoItem,
    onNavigateBack: () -> Unit
) {
    // We need the lifecycle owner so the video pauses if the app goes to the background
    val lifecycleOwner = LocalLifecycleOwner.current

    // Intercept the TV remote back button to close the player and return to Details
    BackHandler {
        onNavigateBack()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { context ->
                YouTubePlayerView(context).apply {
                    // Binds the player to the Compose lifecycle to prevent memory leaks
                    lifecycleOwner.lifecycle.addObserver(this)

                    // Listen for when the internal player is ready, then load the video!
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            // "loadVideo" automatically plays it. (Use cueVideo if you want it to wait)
                            youTubePlayer.loadVideo(video.id, 0f)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}