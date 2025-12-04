package com.example.tiktoklive.manager

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

object LivePlayerManager {
    private var player: ExoPlayer? = null
    private var currentUrl: String? = null

    fun getPlayer(context: Context): ExoPlayer{
        if (player==null){
            player = ExoPlayer.Builder(context.applicationContext).build()
        }
        return player!!
    }

    fun warmUp(context: Context,url: String){
        if (player==null){
            getPlayer(context)
        }
        if (currentUrl==url) return
        currentUrl = url

        val mediaItem = MediaItem.fromUri(url)
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }
    // 暂离
    fun pause() {
        player?.playWhenReady = false
    }
    // 退出应用时
    fun release(){
        player?.release()
        player = null
        currentUrl = null
    }
    // 退出直播间时
    fun stop(){
        player?.stop()
        player?.clearMediaItems()
        currentUrl = null
    }
}