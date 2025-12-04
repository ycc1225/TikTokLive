package com.example.tiktoklive

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktoklive.ui.screens.LiveRoomActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnter = findViewById<Button>(R.id.btn_enter_live_room)

        btnEnter.setOnClickListener {
            val intent = Intent(this, LiveRoomActivity::class.java)
            startActivity(intent)
        }
    }
}