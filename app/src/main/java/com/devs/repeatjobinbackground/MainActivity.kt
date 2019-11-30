package com.devs.repeatjobinbackground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Deven on 30/11/19.
 */
class MainActivity : AppCompatActivity() {

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
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }
}
