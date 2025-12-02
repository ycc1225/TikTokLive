package com.example.tiktoklive.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.tiktoklive.R

class LiveRoomActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    private val URL = "https://livesim2.dashif.org/livesim2/chunkdur_1/ato_7/testpic4_8s/Manifest300.mpd"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_room)
        playerView = findViewById(R.id.player_view)
    }

    override fun onStart() {
        super.onStart()
        if (player==null){
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (player==null){
            initializePlayer()
        }
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initializePlayer(){
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                playerView?.player = this
                val mediaItem = MediaItem.fromUri(URL)
                setMediaItem(mediaItem)
                prepare()
                addListener(
                    object : Player.Listener{
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            when(playbackState){
                                Player.STATE_BUFFERING -> {Log.d("LiveRoom","缓存")}
                                Player.STATE_READY -> {Log.d("LiveRoom","就绪")}
                                Player.STATE_ENDED -> {Log.d("LiveRoom","结束")}
                            }
                        }

                        override fun onPlayerError(error: PlaybackException) {
                            Log.e("LiveRoom","错误${error.message},${error.cause}")
                        }
                    }
                )
            }
    }

    private fun releasePlayer(){
        player?.release()
        player = null
    }
}