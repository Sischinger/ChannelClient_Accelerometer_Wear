package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.example.myapplication.connectionHandling.ConnectionHandler

class MainWearActivity : Activity() {

    private lateinit var connectionHandler: ConnectionHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get an instance of the SensorManager
        connectionHandler = ConnectionHandler(applicationContext)

    }
}
