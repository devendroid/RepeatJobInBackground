package com.devs.repeatjobinbackground

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler


/**
 * Created by Deven on 30/11/19.
 */
class ForegroundService : Service() {

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val INTERVAL_SECONDS = 4L
    private var mHandler: Handler? = null
    private var mNotificationManager: NotificationManager? = null
    private var notification: NotificationCompat.Builder? = null

    private var counter = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // Init
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
         notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)

        mHandler = Handler()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;

        // Start stuffs
        startForeground(1, notification?.build())
        startRepeatingTask()

        return Service.START_NOT_STICKY
    }

    fun startRepeatingTask() {
        mStatusChecker.run()
    }

    fun stopRepeatingTask() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                // Do repeat task here
                notification?.setContentText("Counter "+counter++);
                mNotificationManager?.notify(1, notification?.build())
            }
            finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler?.postDelayed(this, INTERVAL_SECONDS*1000)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}