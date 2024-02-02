package com.gabrielsanchez.ac703

import android.media.browse.MediaBrowser.MediaItem
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.gabrielsanchez.ac703.ui.theme.AC703Theme

class MainActivity : ComponentActivity() {
    private var orientationEventListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AC703Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLandscape by remember { mutableStateOf(false) }
                    orientationEventListener = createOrientationEventListener { isLandscape = it }
                    VideoPlayer(Modifier.fillMaxSize(), onOrientationChanged = { isLandscape = it })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationEventListener?.disable()
    }

    private fun createOrientationEventListener(onOrientationChanged: (Boolean) -> Unit): OrientationEventListener {
        return object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                val isLandscape = orientation in 45..135 || orientation in 225..315
                onOrientationChanged(isLandscape)
            }
        }.apply {
            enable()
        }
    }
}


@Composable
fun VideoPlayer(modifier: Modifier = Modifier, onOrientationChanged: (Boolean) -> Unit) {
    val exoPlayer: ExoPlayer
    val playerView: PlayerView
    val context = LocalContext.current

    exoPlayer = ExoPlayer.Builder(context).build()
    val mediaItem = androidx.media3.common.MediaItem.fromUri("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4")

    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.playWhenReady

    Box (modifier = modifier) {
        DisposableEffect(key1 = Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
        AndroidView(factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                // Force landscape mode if required
                onOrientationChanged(false)
            }
        })
    }
}