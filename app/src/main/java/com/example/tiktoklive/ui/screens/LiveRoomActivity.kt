package com.example.tiktoklive.ui.screens

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.tiktoklive.R
import com.example.tiktoklive.ui.adapter.ChatAdapter
import com.example.tiktoklive.ui.viewmodel.LiveRoomViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val URL = "https://livesim2.dashif.org/livesim2/chunkdur_1/ato_7/testpic4_8s/Manifest300.mpd"
@AndroidEntryPoint
class LiveRoomActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    // 注入ViewModel
    private val viewModel: LiveRoomViewModel by viewModels()

    // 主播信息
    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvFollowers: TextView
    // 评论相关
    private lateinit var rvCommentList: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnSend: Button

    private val chatAdapter = ChatAdapter()
    // 同接人数
    private lateinit var tvOnlineCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_room)
        // 初始化view
        initializeViews()
        setupRecyclerView()
        setupListener()
        observeViewModel()
        viewModel.fetchHostInfo("5")
        viewModel.fetchComments()
        viewModel.startWebSocket()
    }

    private fun initializeViews() {
        // 播放器
        playerView = findViewById(R.id.player_view)
        // 用户信息
        ivAvatar = findViewById(R.id.iv_host_avatar)
        tvName = findViewById(R.id.tv_host_name)
        tvFollowers = findViewById(R.id.tv_follower_count)
        // 评论相关
        rvCommentList = findViewById(R.id.rv_chat_list)
        etComment = findViewById(R.id.et_comment)
        btnSend = findViewById(R.id.btn_send)
        // 同接人数
        tvOnlineCount = findViewById(R.id.tv_online_count)
    }
    private fun setupRecyclerView(){
        rvCommentList.apply {
            layoutManager = LinearLayoutManager(this@LiveRoomActivity)
            adapter = chatAdapter
        }
    }
    private fun setupListener(){
        btnSend.setOnClickListener {
            val content = etComment.text.toString().trim()
            if (content.isNotEmpty()){
                viewModel.sendComment(content)
            }else{
                Toast.makeText(this,"内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.hostInfo.observe(this) { host ->
            tvName.text = host.name
            tvFollowers.text = "${host.followerCount} 关注"
            Glide.with(this)
                .load(host.avatarUrl)
                .transform(CircleCrop()) // 圆形裁剪
                .placeholder(R.color.uiBackground)
                .into(ivAvatar)

            Log.d("LiveRoom", "Host loaded: ${host.name}")
        }
        viewModel.comments.observe(this) {comments ->
            chatAdapter.setMessages(comments)
            if (comments.isNotEmpty()){
                rvCommentList.scrollToPosition(comments.size - 1)
            }
        }
        viewModel.sendSuccess.observe(this){success->
            if (success) {
                etComment.text.clear()
            }
        }
        viewModel.onlineCount.observe(this){count->
            tvOnlineCount.text = "$count"
        }
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
                        override fun onPlayerError(error: PlaybackException) {
                            Toast.makeText(applicationContext,"播放错误:${error.message}", Toast.LENGTH_SHORT).show()
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