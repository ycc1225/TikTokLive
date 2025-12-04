package com.example.tiktoklive.ui.screens

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.animation.addListener
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.tiktoklive.R
import com.example.tiktoklive.ui.component.LiveComponentManager
import com.example.tiktoklive.ui.component.impl.ChatComponent
import com.example.tiktoklive.ui.component.impl.InputComponent
import com.example.tiktoklive.ui.component.impl.UserInfoComponent
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val URL = "https://livesim2.dashif.org/livesim2/chunkdur_1/ato_7/testpic4_8s/Manifest300.mpd"
private const val TAG = "LiveRoom"
@AndroidEntryPoint
class LiveRoomActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    // 注入ViewModel
    private val viewModel: LiveRoomViewModel by viewModels()

    // 组件管理
    private var componentManager: LiveComponentManager? =null
    private var isUILoaded = false

    private var startTime:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis()
        Log.d("PerformanceTest","T1 - $startTime")
        setContentView(R.layout.activity_live_room)
        // 初始化view
        playerView = findViewById(R.id.player_view)
        viewModel.fetchHostInfo("5")
        viewModel.fetchComments()
        viewModel.startWebSocket()
        viewModel.errorMsg.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
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
                        override fun onRenderedFirstFrame() {
                            val videoTime = System.currentTimeMillis()
                            val duration = videoTime - startTime
                            Log.d("PerformanceTest", "T2 - Video First Frame Rendered: $videoTime")
                            Log.d("PerformanceTest", "Cost(Activity Start -> Video): ${duration}ms")
                            if (!isUILoaded){
                                loadInteractionLayerAsync()
                            }
                        }
                        override fun onPlayerError(error: PlaybackException) {
                            Toast.makeText(applicationContext,"播放错误:${error.message}", Toast.LENGTH_SHORT).show()
                            Log.e(TAG,"错误${error.message},${error.cause}")
                        }
                    }
                )
            }
    }

    private fun loadInteractionLayerAsync(){
        Log.d(TAG,"开始加载交互层")
        isUILoaded = true
        val container = findViewById<FrameLayout>(R.id.fl_interaction_container)
        val asyncInflater = AsyncLayoutInflater(this)
        asyncInflater.inflate(R.layout.layout_live_room_interaction,container){ view, _, _ ->
            view.alpha = 0f
            container.addView(view)
            componentManager = LiveComponentManager(view as ViewGroup,viewModel,this@LiveRoomActivity)
            componentManager?.register(UserInfoComponent())
            componentManager?.register(ChatComponent())
            componentManager?.register(InputComponent())
            val uiTime = System.currentTimeMillis()
            Log.d("PerformanceTest", "T3 - UI Inflated: $uiTime")
            Log.d("PerformanceTest", "Cost(Video -> UI): ${uiTime - startTime}ms")
            ObjectAnimator.ofFloat(view,"alpha",0f,1f).apply {
                duration = 500
                addListener(onEnd = {
                    Log.d("PerformanceTest", "T4 - UI Animation Ended (Fully Visible): ${System.currentTimeMillis()}")
                })
                start()
            }
        }
        Log.d(TAG,"交互层加载完毕")
    }

    private fun releasePlayer(){
        player?.release()
        player = null
    }
}