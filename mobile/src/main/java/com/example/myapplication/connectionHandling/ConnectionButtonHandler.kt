package com.example.myapplication.connectionHandling

import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.mysharedvariables.Constants

class ConnectionButtonHandler(private val binding: ActivityMainBinding,
                              private val activity: AppCompatActivity,
                              private val connection: ConnectionHandler) : ErrorListener_ConnectionHandler {

    private val TAG = "com.example.myapplication.connectionHandling.ButtonHandler"
    private var waitingSeconds: Long = 0
    init {
        connection.addErrorListener(this)

        binding.openCloseChannelButton.setOnClickListener {
            when (binding.openCloseChannelButton.text) {
                activity.getString(R.string.open_channel) -> {
                    updateOpenCloseButtonState(R.string.connecting_channel, false)
                    connection.channelService.openChannel()
                }
                else -> {
                    updateOpenCloseButtonState(R.string.closing_channel, false)
                    connection.channelService.closeChannel()
                }
            }
        }

        ConnectionButtonHandlerErrors(this, activity, connection.channelService, connection.inputStream)

    }

    fun updateOpenCloseButtonState(textId: Int, isEnabled: Boolean) {
        binding.openCloseChannelButton.apply {
            text = activity.getString(textId)
            this.isEnabled = isEnabled
        }
    }

    override fun onWaitingForUserInput() {
        waitingSeconds += Constants.OBSERVATION_LOOP_INTERVAL
        if (waitingSeconds.toInt()%2000 == 0){
            Log.d(TAG, "onWaitingForUserInput: ")
        }
        activity.runOnUiThread() {
            updateOpenCloseButtonState(R.string.open_channel, true)
        }
    }

    override fun onInputStreamActive(status: Boolean) {
        Log.d(TAG, "onInputStreamActive: $status")
        if(status){
            activity.runOnUiThread() {
                updateOpenCloseButtonState(R.string.close_channel, true)
            }
        }
        else{
            activity.runOnUiThread() {
                updateOpenCloseButtonState(R.string.waiting_for_Wear_Connection, false)
            }
            connection.channelService.restartChannel()
        }
    }

}