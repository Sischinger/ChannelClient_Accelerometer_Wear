package com.example.mysharedvariables

object Constants {
    val CHANNEL_MSG = "com.example.android.wearable.datalayer.channelmessage"

    // Error messages ChannelService
    val CHANNEL_COULD_NOT_BE_OPENED = "Channel could not be opened"
    val NO_CHANNELS_FOUND_TO_BE_OPENED = "No channels found to be opened"
    val NO_CHANNELS_FOUND_TO_BE_CLOSED = "No channels found to be closed"
    val MORE_THAN_ONE_CONNECTED_DEVICE_FOUND = "More than one connected device found"

    // Error messages Output/InputStreamService
    val OUTPUTSTREAM_COULD_NOT_BE_OPENED = "OutputStream could not be opened"
    val INPUTSTREAM_COULD_NOT_BE_OPENED = "InputStream could not be opened"

    // InputStream read status
    val INPUTSTREAM_READ_STATUS_NOT_DEFINED = "InputStream read status not defined"
    val WAITING_FOR_DATA = "Waiting for data"
    val DATA_RECEIVED = "Data received"

    val OBSERVATION_LOOP_INTERVAL: Long = 500 //ms

}