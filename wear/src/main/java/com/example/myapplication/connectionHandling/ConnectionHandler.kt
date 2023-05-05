package com.example.myapplication.connectionHandling

import DataSendService
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.OutputStream

class ConnectionHandler(context: Context):   ChannelServiceListener, OutputStreamListener, InputStreamListener {

    private val TAG = "connectionHandler"
    private val channelClient = Wearable.getChannelClient(context)
    private val context = context
    var dataSendService: DataSendService? = null
    val channelService = ChannelService(channelClient)
    val outputStreamService = OutputStreamService(channelClient, channelService)
    val inputStreamService = InputStreamService(channelClient, channelService)

    init {
        channelService.addListener(this)
        outputStreamService.addListener(this)
        inputStreamService.addListener(this)
    }

    //// Channel Interface ////
    override fun onChannelOpened(channel: ChannelClient.Channel) {
        Log.d(TAG, "onChannelOpened: ")
    }

    override fun onChannelClosed() {
        Log.d(TAG, "onChannelClosed: ")
    }


    //// OutputStream Interface ////
    override fun onOutputStreamCreated(outputStream: OutputStream) {
        Log.d(TAG, "onOutputStreamCreated: ")
        dataSendService = DataSendService(context, outputStream)
        dataSendService?.start()
    }

    override fun onOutputStreamDeleted() {
        Log.d(TAG, "onOutputStreamDeleted: ")
        dataSendService?.unregisterListener()
        dataSendService?.interrupt()
    }

    //// InputStream Interface ////
    override fun onInputStreamCreated() {
        Log.d(TAG, "onInputStreamCreated: ")
    }

    override fun onInputStreamDeleted() {
        Log.d(TAG, "onInputStreamDeleted: ")
    }


}