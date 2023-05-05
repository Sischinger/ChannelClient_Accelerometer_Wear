package com.example.myapplication.connectionHandling

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.mysharedvariables.Constants
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.InputStream
import java.util.*

interface ConnectionHandlerListener{
    fun onNewReceivedDataAvailable(x: Float, y: Float, z: Float, iter: Int)
}
interface ErrorListener_ConnectionHandler{
    fun onWaitingForUserInput()
    fun onInputStreamActive(status: Boolean)
}
class ConnectionHandler(context: Context):   InputStreamListener, ReceiveDataListener{

    private val TAG = "connectionHandler"
    private val channelClient = Wearable.getChannelClient(context)
    private var receiveDataService: ReceiveDataService? = null
    private val listeners = mutableListOf<ConnectionHandlerListener>()
    private val error_listeners = mutableListOf<ErrorListener_ConnectionHandler>()
    val channelService = ChannelService(context, channelClient)
    val inputStream = InputStreamService(channelClient, channelService)

    init {
        inputStream.addListener(this)
        startChannelObserver()
    }

    fun addListener(listener: ConnectionHandlerListener) {
        listeners.add(listener)
    }
    fun addErrorListener(errorListener: ErrorListener_ConnectionHandler) {
        error_listeners.add(errorListener)
    }

    //// InputStream Interface ////
    override fun onInputStreamCreated(inputStream: InputStream) {
        Log.d(TAG, "onInputStreamCreated: ")
        receiveDataService = ReceiveDataService(inputStream)
        receiveDataService?.addListener(this)
        receiveDataService?.start()
    }

    override fun onInputStreamDeleted() {
        Log.d(TAG, "onInputStreamDeleted: ")
        receiveDataService?.interrupt()
    }

    fun startChannelObserver() {
        val channelObserver = Timer()
        val channelObserverTask = object : TimerTask() {
            var inputStreamIter_Temp = -1
            override fun run() {

                if(channelService.isChannelOpen){
                    if(receiveDataService?.inputStreamIter != inputStreamIter_Temp){
                        error_listeners.forEach { listener ->
                            listener.onInputStreamActive(true)
                        }
                    }
                    else{
                        error_listeners.forEach { listener ->
                            listener.onInputStreamActive(false)
                        }
                    }
                    inputStreamIter_Temp = receiveDataService?.inputStreamIter!!
                }
                else{
                    error_listeners.forEach { listener ->
                        listener.onWaitingForUserInput()
                    }
                }

            }
        }
        channelObserver.schedule(channelObserverTask, 0, Constants.OBSERVATION_LOOP_INTERVAL)
    }

    override fun onReceiveData(x: Float, y: Float, z: Float, iter: Int) {
        listeners.forEach { listener ->
            listener.onNewReceivedDataAvailable(x, y, z, iter)
        }
    }
}