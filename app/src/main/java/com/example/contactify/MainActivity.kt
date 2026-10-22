package com.example.contactify

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.contactify.navigation.AppNavigation
import com.example.contactify.service.MusicService
import com.example.contactify.ui.theme.ContactifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ── Khởi động MusicService ─────────────────────────────────────────
        // Service chạy foreground, không bị kill bởi system
        // Lưu ý: cần FOREGROUND_SERVICE permission trong AndroidManifest
        startMusicService()

        setContent {
            // Dark mode state — lưu trong Compose State
            var isDarkMode by remember { mutableStateOf(false) }

            ContactifyTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }

    private fun startMusicService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        // Dừng MusicService khi Activity bị destroy
        val serviceIntent = Intent(this, MusicService::class.java)
        stopService(serviceIntent)
        super.onDestroy()
    }
}
