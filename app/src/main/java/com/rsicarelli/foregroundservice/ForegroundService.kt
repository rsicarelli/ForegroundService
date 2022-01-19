package com.rsicarelli.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

const val NOTIFICATION_ID = 4432

class ForegroundService : Service() {
    private var isRunning = false

    private val binder = ForegroundServiceBinder()

    val defaultNotification = {
        NotificationCompat.Builder(this, getChannel())
            .setContentTitle("Connected")
            .setContentText("Service is running")
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setVibrate(longArrayOf(0L)) //remove vibration
            .setColor(ContextCompat.getColor(this, R.color.purple_700))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // maybe a different priority to be less intrusive
            .setOngoing(true) //important stuff! Notification cannot be dismissed
            .setAutoCancel(true)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int = START_STICKY

    private fun forceForeground() {
        val intent = Intent(this, ForegroundService::class.java)
        ContextCompat.startForegroundService(
            this,
            intent
        ) //Handles startForeground for different versions
        startForeground(
            NOTIFICATION_ID,
            defaultNotification().build()
        ) //Force foreground to display notification
    }

    override fun onBind(intent: Intent?): ForegroundServiceBinder {
        return binder.apply { onBind(this@ForegroundService) }
    }

    fun start() {
        if (isRunning) {
            //Service might be started multiple times.
            // Simple flagging to avoid multiple instantiations
            return
        }

        isRunning = true

        GlobalScope.launch {
            launchPeriodicAsync(1000) {
                println("Hi faru")
            }.await()
        }

        forceForeground()
    }

    private fun stop() {
        isRunning = false
        // unregister stuff. Free up memory

        if (isOreo()) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    private fun getChannel() = if (isOreo()) createNotificationChannel() else ""

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "some_id"
        val channelName = "Foreground Service"
        val notificationChannel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lightColor = ContextCompat.getColor(this@ForegroundService, R.color.purple_200)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(notificationChannel)
        return channelId
    }

}

class ForegroundServiceBinder : Binder() {

    private var weakService: WeakReference<ForegroundService>? = null

    fun getService(): ForegroundService? {
        //Useful if you want to retrieve the service from a Screen
        return weakService?.get()
    }

    fun onBind(service: ForegroundService) {
        this.weakService = WeakReference(service)
    }
}

fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

/*
* The ugly but right way to start a foreground Service
* "After the system has created the service, the app has five seconds to call the service's startForeground()
* method to show the new service's user-visible notification.
* If the app does not call startForeground() within the time limit,
* the system stops the service and declares the app to be ANR."
*
* https://developer.android.com/about/versions/oreo/background#services
*
* By doing in that way, its guaranteed that the service will start and attach a notification right on time
*
* */
fun Context.startForegroundService() {
    val intent = Intent(this, ForegroundService::class.java)
    applicationContext.bindService(intent, object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName,
            binder: IBinder
        ) {
            if (binder is ForegroundServiceBinder) {
                binder.getService()?.start()
            }
            applicationContext.unbindService(this)
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }, Context.BIND_AUTO_CREATE)
}

fun CoroutineScope.launchPeriodicAsync(
    repeatMillis: Long,
    action: () -> Unit
) = this.async {
    if (repeatMillis > 0) {
        while (isActive) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}