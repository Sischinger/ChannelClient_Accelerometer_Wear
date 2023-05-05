package com.example.myapplication.connectionHandling

import android.content.Context
import android.util.Log
import com.example.mysharedvariables.Constants
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.tasks.Tasks
import kotlin.concurrent.thread

interface ChannelServiceListener {
    fun onChannelOpened(channel: ChannelClient.Channel)
    fun onChannelClosed()
}

interface ErrorListener_ChannelService {
    fun onChannelServiceError(errorMsg: String)
}

class ChannelService(private val context: Context,
                     private val channelClient: ChannelClient) {

    private val TAG = "ChannelService"
    private val listeners = mutableListOf<ChannelServiceListener>()
    private val error_listeners = mutableListOf<ErrorListener_ChannelService>()
    private var openedChannel: ChannelClient.Channel? = null
    var isChannelOpen = false

    init{
        registerChannelClosedCallback()
    }
    fun addListener(listener: ChannelServiceListener) {
        listeners.add(listener)
    }

    fun addErrorListener(errorListener: ErrorListener_ChannelService) {
        error_listeners.add(errorListener)
    }

    fun openChannel() = thread(start = true) {
        val nodes = getNodes(context)
        if (nodes.isNotEmpty()) {
            if(nodes.size == 1) {
                nodes.forEach { node ->
                    channelClient.openChannel(node, Constants.CHANNEL_MSG).addOnSuccessListener { channel ->
                        openedChannel = channel
                        isChannelOpen = true
                        listeners.forEach { listener ->
                            listener.onChannelOpened(channel)
                        }
                    }.addOnFailureListener {
                        error_listeners.forEach { listener ->
                            listener.onChannelServiceError(Constants.CHANNEL_COULD_NOT_BE_OPENED)
                        }
                    }
                }
            }
            else{
                error_listeners.forEach { listener ->
                    listener.onChannelServiceError(Constants.MORE_THAN_ONE_CONNECTED_DEVICE_FOUND)
                }
            }
        } else {
            error_listeners.forEach { listener ->
                listener.onChannelServiceError(Constants.NO_CHANNELS_FOUND_TO_BE_OPENED)
            }
        }
    }

    fun closeChannel() {
        if(openedChannel != null) {
            channelClient.close(openedChannel!!)
            openedChannel = null
        }
        else{
            error_listeners.forEach { listener ->
                listener.onChannelServiceError(Constants.NO_CHANNELS_FOUND_TO_BE_CLOSED)
            }
        }
    }

    private fun registerChannelClosedCallback() {
        channelClient.registerChannelCallback(object : ChannelClient.ChannelCallback() {
            override fun onChannelClosed(channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int) {
                super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
                isChannelOpen = false
                listeners.forEach { listener ->
                    listener.onChannelClosed()
                }
            }
        })
    }
    private fun getNodes(context: Context): HashSet<String> {
        val results = HashSet<String>()
        val nodeListTask = Wearable.getNodeClient(context).connectedNodes
        try {
            val nodes = Tasks.await(nodeListTask)
            nodes.mapTo(results) { it.id }
        } catch (exception: Exception) {
            Log.e(TAG, "get nodes failed", exception)
        }
        return results
    }

    fun restartChannel() {
        closeChannel()
        openChannel()
    }
}
