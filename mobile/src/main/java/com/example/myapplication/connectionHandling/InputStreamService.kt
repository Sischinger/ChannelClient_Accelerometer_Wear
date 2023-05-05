package com.example.myapplication.connectionHandling

import com.example.mysharedvariables.Constants
import com.google.android.gms.wearable.ChannelClient
import java.io.InputStream

interface InputStreamListener {
    fun onInputStreamCreated(inputStream: InputStream)
    fun onInputStreamDeleted()
}

interface ErrorListener_InputStream {
    fun onInputStreamError(errorMsg: String)
}
class InputStreamService(private val channelClient: ChannelClient,
                          private val channelService: ChannelService): ChannelServiceListener {

    private val TAG = "InputStreamService"
    private var inputStream: InputStream? = null
    private val listeners = mutableListOf<InputStreamListener>()
    private val error_listeners = mutableListOf<ErrorListener_InputStream>()
    init {
        channelService.addListener(this)
        registerInputStreamCallback()
    }

    fun addListener(listener: InputStreamListener) {
        listeners.add(listener)
    }

    fun addErrorListener(errorListener: ErrorListener_InputStream) {
        error_listeners.add(errorListener)
    }

    private fun registerInputStreamCallback() {
        channelClient.registerChannelCallback(object : ChannelClient.ChannelCallback() {
            override fun onInputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
                inputStream = null
                listeners.forEach { listener ->
                    listener.onInputStreamDeleted()
                }
            }
        })
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        channelClient.getInputStream(channel).addOnSuccessListener { inStream ->
            listeners.forEach { listener ->
                listener.onInputStreamCreated(inStream)
            }
        }.addOnFailureListener {
            error_listeners.forEach { listener ->
                listener.onInputStreamError(Constants.INPUTSTREAM_COULD_NOT_BE_OPENED)
            }
        }
    }

    override fun onChannelClosed() {
        inputStream?.close()
    }
}