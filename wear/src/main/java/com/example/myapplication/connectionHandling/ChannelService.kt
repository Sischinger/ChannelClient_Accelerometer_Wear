package com.example.myapplication.connectionHandling

import com.google.android.gms.wearable.ChannelClient

interface ChannelServiceListener {
    fun onChannelOpened(channel: ChannelClient.Channel)
    fun onChannelClosed()
}
class ChannelService(private val channelClient: ChannelClient) {

    private val listeners = mutableListOf<ChannelServiceListener>()

    init {
        registerChannelCallbacks()
    }

    fun addListener(listener: ChannelServiceListener) {
        listeners.add(listener)
    }

    fun registerChannelCallbacks() {
        channelClient.registerChannelCallback(object : ChannelClient.ChannelCallback() {
            override fun onChannelOpened(channel: ChannelClient.Channel) {
                super.onChannelOpened(channel)
                listeners.forEach { listener ->
                    listener.onChannelOpened(channel)
                }
            }
            override fun onChannelClosed(channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int) {
                super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
                listeners.forEach { listener ->
                    listener.onChannelClosed()
                }
            }
        })
    }
}