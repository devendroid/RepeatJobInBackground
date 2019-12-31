package com.devs.repeatjobinbackground

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import androidx.core.content.ContextCompat
import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


/**
 * Created by Deven on 30/11/19.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonStartService.setOnClickListener() {
            startService()
        }

        buttonStopService.setOnClickListener() {
            stopService()
        }
    }

    fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        serviceIntent.putExtra("reciever", MyResultReceiver(Handler(), this))
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }

    class MyResultReceiver(handler: Handler, activity: MainActivity)
        : ResultReceiver(handler){

        var activityWeekRef: WeakReference<MainActivity>? = null

        init {
            activityWeekRef = WeakReference(activity)
        }

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Activity.RESULT_OK) {
                Log.i("MyResultReceiver","===result "+resultData?.getInt("number"))
                activityWeekRef?.get()?.tv_result?.text = resultData?.getInt("number").toString()
            }
        }
    }
}
