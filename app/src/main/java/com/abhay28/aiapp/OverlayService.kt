package com.abhay28.aiapp
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.app.NotificationCompat

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0

        windowManager.addView(overlayView, layoutParams)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "overlay_service_channel"
            val channel = NotificationChannel(
                channelId,
                "Overlay Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Overlay Service")
                .setContentText("Overlay is running")
//                .setSmallIcon(R.drawable.ic_overlay)
                .build()
            startForeground(1, notification)
        }

        // Set up the button to launch the app
        val launchButton: Button = overlayView.findViewById(R.id.overlay_button)
        launchButton.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
