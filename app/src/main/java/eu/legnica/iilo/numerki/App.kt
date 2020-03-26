package eu.legnica.iilo.numerki

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NUMBER_CHANNEL_PUSH_ID,
                getString(R.string.notify_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.notify_channel_description)
            channel.enableVibration(true)
            channel.enableLights(true)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NUMBER_CHANNEL_PUSH_ID = "notify_channel"
    }
}