package com.example.gameshub.presentation.details.components

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayer(
    videoId: String?,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val playerState = remember { mutableStateOf<YouTubePlayer?>(null) }
    val playerViewState = remember { mutableStateOf<YouTubePlayerView?>(null) }
    var lastLoadedId by remember { mutableStateOf<String?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            YouTubePlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                lifecycleOwner.lifecycle.addObserver(this)

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        playerState.value = youTubePlayer
                    }
                })

                playerViewState.value = this
            }
        },
        update = {
            val player = playerState.value ?: return@AndroidView

            if (videoId != null && videoId != lastLoadedId) {
                lastLoadedId = videoId

                if (autoPlay) {
                    player.loadVideo(videoId, 0f)
                } else {
                    player.cueVideo(videoId, 0f)
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            playerViewState.value?.let { view ->
                lifecycleOwner.lifecycle.removeObserver(view)
                view.release()
            }
            playerState.value = null
            playerViewState.value = null
        }
    }
}