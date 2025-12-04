package com.example.tiktoklive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktoklive.manager.LivePlayerManager
import com.example.tiktoklive.ui.screens.LiveRoomActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val liveUrl = "https://livesim2.dashif.org/livesim2/chunkdur_1/ato_7/testpic4_8s/Manifest300.mpd"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnter = findViewById<Button>(R.id.btn_enter_live_room)

        btnEnter.setOnClickListener {
            val intent = Intent(this, LiveRoomActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Main","Resume")
        LivePlayerManager.warmUp(this,liveUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        LivePlayerManager.release()
    }
}