package dev.nsomnia.chadsuno.ui.screens.player

import android.content.ComponentName
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dev.nsomnia.chadsuno.domain.model.Song

@Composable
fun rememberPlayerState(): PlayerState {
    val context = LocalContext.current
    val playerState = remember { PlayerState() }

    DisposableEffect(Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()

        playerState.controllerFuture = controllerFuture

        Futures.addCallback(controllerFuture, object : FutureCallback<MediaController> {
            override fun onSuccess(controller: MediaController) {
                playerState.setupListener(controller)
            }
            override fun onFailure(t: Throwable) {
                t.printStackTrace()
            }
        }, context.mainExecutor)

        onDispose {
            controllerFuture.get()?.release()
        }
    }

    return playerState
}

class PlayerState {
    private var _currentSong: MutableState<Song?> = mutableStateOf(null)
    val currentSong: Song? by _currentSong

    private var _isPlaying: MutableState<Boolean> = mutableStateOf(false)
    val isPlaying: Boolean by _isPlaying

    private var _position: MutableState<Long> = mutableStateOf(0L)
    val position: Long by _position

    private var _duration: MutableState<Long> = mutableStateOf(0L)
    val duration: Long by _duration

    private var _queue: MutableState<List<Song>> = mutableStateOf(emptyList())
    val queue: List<Song> by _queue

    var controllerFuture: ListenableFuture<MediaController>? = null

    private var controller: MediaController? = null

    internal fun setupListener(ctrl: MediaController) {
        controller = ctrl
        ctrl.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                _duration.value = ctrl.duration
            }

            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
                _position.value = ctrl.currentPosition
            }
        })
    }

    fun play(song: Song) {
        _currentSong.value = song
        val ctrl = controller ?: return
        song.audioUrl?.let { url ->
            ctrl.setMediaItem(MediaItem.fromUri(url))
            ctrl.prepare()
            ctrl.playWhenReady = true
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        _queue.value = songs
        val ctrl = controller ?: return

        val mediaItems = songs.mapNotNull { song ->
            song.audioUrl?.let { MediaItem.fromUri(it) }
        }

        ctrl.setMediaItems(mediaItems)
        ctrl.prepare()
        ctrl.seekToDefaultPosition(startIndex)
        ctrl.playWhenReady = true
        _currentSong.value = songs.getOrNull(startIndex)
    }

    fun togglePlayPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) {
            ctrl.pause()
        } else {
            ctrl.play()
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun next() {
        controller?.seekToNextMediaItem()
    }

    fun previous() {
        controller?.seekToPreviousMediaItem()
    }
}
