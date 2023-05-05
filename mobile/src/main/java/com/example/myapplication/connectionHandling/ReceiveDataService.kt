package com.example.myapplication.connectionHandling

import android.util.Log
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.mysharedvariables.Constants
import com.google.android.gms.wearable.ChannelIOException
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

interface ReceiveDataListener{
    fun onReceiveData(x: Float, y: Float, z: Float, iter: Int)
}
class ReceiveDataService(private val inputStream: InputStream): Thread() {

    private val TAG = "ReceiveDataService"
    private val dataInputStream = DataInputStream(inputStream)
    private val listeners = mutableListOf<ReceiveDataListener>()
    var inputStreamIter = 0

    fun addListener(listener: ReceiveDataListener) {
        listeners.add(listener)
    }

    override fun run() {
        while(!isInterrupted){
            if(!readDataFloats(dataInputStream)){
                break
            }
        }
    }

    private fun readDataFloats(dataInputStream: DataInputStream): Boolean {
        return try {
            inputStreamIter++
            val x = dataInputStream.readFloat()
            val y = dataInputStream.readFloat()
            val z = dataInputStream.readFloat()
            listeners.forEach { listener ->
                listener.onReceiveData(x, y, z, inputStreamIter)
            }
            true
        } catch (e: IOException) {
            if (e is ChannelIOException) {
                Log.d(TAG, "Channel closed properly")
            } else {
                Log.e(TAG, "Error reading from inputStream: $e")
            }
            false
        }
    }
}
