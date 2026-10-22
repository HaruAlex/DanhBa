package com.example.contactify.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.contactify.R

/**
 * Service phát nhạc nền.
 * Chạy ở foreground với notification để không bị kill bởi hệ thống.
 * 
 * Lưu ý: Do không có file nhạc thực tế, service này chỉ là demo skeleton.
 * Trong production, bạn cần:
 * - Thêm file mp3 vào res/raw/
 * - Khởi tạo MediaPlayer với file đó: MediaPlayer.create(this, R.raw.your_song)
 * - Xử lý lifecycle đúng cách (pause, resume, stop)
 */
class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicServiceChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Khởi tạo MediaPlayer với file nhạc
        // Lưu ý: Đổi 'background_music' thành tên file thực tế của bạn
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.bien_dao_va_em)
            mediaPlayer?.isLooping = true       // Lặp lại vô hạn
            mediaPlayer?.setVolume(0.3f, 0.3f)  // Âm lượng 30%
        } catch (e: Exception) {
            e.printStackTrace()
            // Nếu không tìm thấy file, service vẫn chạy nhưng không phát nhạc
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Bắt đầu phát nhạc nếu chưa đang phát
        try {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Dịch vụ phát nhạc nền của Contactify"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Contactify Music")
            .setContentText("Đang phát nhạc nền...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }
}
