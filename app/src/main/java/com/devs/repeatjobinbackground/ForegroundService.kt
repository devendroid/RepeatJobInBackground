package com.devs.repeatjobinbackground

import android.app.*
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.content.Context
import android.os.*
import io.reactivex.Observable
import android.util.Log
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.Callable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


/**
 * Created by Deven on 30/11/19.
 *
 */
class ForegroundService : Service() {

    private val TAG = "ForegroundService"
    private var counter = 0
    private var isRunning = false

    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val INTERVAL_SECONDS = 4L
    private var mHandler: Handler? = null
    private var mNotificationManager: NotificationManager? = null
    private var notification: NotificationCompat.Builder? = null
    private var resultReceiver: ResultReceiver? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Init
        resultReceiver = intent.extras?.get("reciever") as ResultReceiver?
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
        if(!isRunning)
        startRepeatingTask()

        return Service.START_NOT_STICKY
    }

    fun startRepeatingTask() {
        isRunning = true
        repeatRunnable.run()
    }

    fun stopRepeatingTask() {
        isRunning = false
        mHandler?.removeCallbacks(repeatRunnable)
    }

    var repeatRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                // Do repeat task here
                doFirstTask()
                    .flatMap { doSecondTask(it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            notification?.setContentText("2xi "+it);
                            mNotificationManager?.notify(1, notification?.build())
                            val resultBundle = Bundle()
                            resultBundle.putInt("number",it)
                            resultReceiver?.send(Activity.RESULT_OK, resultBundle)
                        },
                        { it.printStackTrace()  }
                    )
            }
            finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler?.postDelayed(this, INTERVAL_SECONDS*1000)
            }
        }
    }

    private fun doFirstTask():Observable<Int>{

        val ob1 =  Observable.fromCallable( object : Callable<Int> {
            override fun call() :Int  {
                   return ++counter;
                //else  throw Exception("Your custom message")
            }
         } )

        return ob1
    }

    private fun doSecondTask(input:Int):Observable<Int>{
        val ob2 =  Observable.fromCallable( object : Callable<Int> {
            override fun call() :Int  {
                return input*2;
            }
        } )

        return ob2
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
        Log.i(TAG,"==onDestroy")
        super.onDestroy()
        stopRepeatingTask()
    }

}


