package com.example.myapplication.connectionHandling

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.myapplication.R
import com.example.mysharedvariables.Constants

class ConnectionButtonHandlerErrors(private val buttonHandler: ConnectionButtonHandler,
                                    private val activity: Activity,
                                    private val channelService: ChannelService,
                                    private val inputStreamService: InputStreamService): ErrorListener_InputStream, ErrorListener_ChannelService {
    private val TAG = "connectionErrorHandling"

    init {
        channelService.addErrorListener(this)
        inputStreamService.addErrorListener(this)
    }

    override fun onInputStreamError(errorMsg: String) {
        Log.d(TAG, "onInputStreamError: $errorMsg")
        activity.runOnUiThread() {
            if (errorMsg == Constants.INPUTSTREAM_COULD_NOT_BE_OPENED) {
                Toast.makeText(activity, "InputStream could not be opened", Toast.LENGTH_SHORT).show()
                buttonHandler.updateOpenCloseButtonState(R.string.open_channel, true)
            }
        }
    }

    override fun onChannelServiceError(errorMsg: String) {
        Log.d(TAG, "onChannelServiceError: $errorMsg")
        activity.runOnUiThread() {
            when (errorMsg) {
                "Channel could not be opened" -> {
                    Toast.makeText(activity, "Channel could not be opened", Toast.LENGTH_SHORT).show()
                    buttonHandler.updateOpenCloseButtonState(R.string.open_channel, true)
                }
                "No channels found to be opened" -> {
                    Toast.makeText(activity, "No channels found to be opened. Connect to your Wear Device", Toast.LENGTH_SHORT).show()
                    buttonHandler.updateOpenCloseButtonState(R.string.open_channel, true)
                }
                "No channels found to be closed" -> {
                    Toast.makeText(activity, "No channels found to be closed", Toast.LENGTH_SHORT).show()
                    buttonHandler.updateOpenCloseButtonState(R.string.open_channel, true)
                }
                "More than one connected device found" -> {
                    Toast.makeText(activity, "Multiple connected devices found - Please connect only one", Toast.LENGTH_SHORT).show()
                    buttonHandler.updateOpenCloseButtonState(R.string.open_channel, true)
                }
            }
        }
    }
}