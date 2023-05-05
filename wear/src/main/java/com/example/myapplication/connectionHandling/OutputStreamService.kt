package com.example.myapplication.connectionHandling

import com.example.mysharedvariables.Constants
import com.google.android.gms.wearable.ChannelClient
import java.io.OutputStream

interface OutputStreamListener {
    fun onOutputStreamCreated(outputStream: OutputStream)
    fun onOutputStreamDeleted()
}

interface ErrorListener_OutputStream {
    fun onOutputStreamError(error: String)
}
class OutputStreamService(private val channelClient: ChannelClient,
                          private val channelService: ChannelService): ChannelServiceListener {

    private val TAG = "OutputStreamService"
    private var outputStream: OutputStream? = null
    private val listeners = mutableListOf<OutputStreamListener>()
    private val error_listeners = mutableListOf<ErrorListener_OutputStream>()
    init {
        channelService.addListener(this)
        registerOutputStreamCallback()
    }

    fun addListener(listener: OutputStreamListener) {
        listeners.add(listener)
    }
    fun addErrorListener(errorListener: ErrorListener_OutputStream) {
        error_listeners.add(errorListener)
    }

    private fun registerOutputStreamCallback() {
        channelClient.registerChannelCallback(object : ChannelClient.ChannelCallback() {
            override fun onOutputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
                outputStream = null
                listeners.forEach { listener ->
                    listener.onOutputStreamDeleted()
                }
            }
        })
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        channelClient.getOutputStream(channel).addOnSuccessListener { outStream ->
            listeners.forEach { listener ->
                listener.onOutputStreamCreated(outStream)
            }
        }.addOnFailureListener {
            error_listeners.forEach { listener ->
                listener.onOutputStreamError(Constants.OUTPUTSTREAM_COULD_NOT_BE_OPENED)
            }
        }
    }

    override fun onChannelClosed() {
        outputStream?.close()
    }
}