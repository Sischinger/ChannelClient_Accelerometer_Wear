package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.connectionHandling.ConnectionButtonHandler
import com.example.myapplication.connectionHandling.ConnectionHandler
import com.example.myapplication.connectionHandling.ConnectionHandlerListener
import com.example.myapplication.connectionHandling.ReceiveDataListener
import com.example.myapplication.databinding.ActivityMainBinding

class MainMobileActivity : AppCompatActivity(), ConnectionHandlerListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connection = ConnectionHandler(applicationContext)
        connection.addListener(this)
        ConnectionButtonHandler(binding, this, connection)

    }

    companion object {
        private const val TAG = "MAIN_ACTIVITY_TAG"
    }

    override fun onNewReceivedDataAvailable(x: Float, y: Float, z: Float, iter: Int) {
        Log.d(TAG, "onNewReceivedDataAvailable: $x, $y, $z, $iter")
        binding.tvMessage.text = "input Iter: $iter \nX: $x\n Y: $y\n Z: $z"
    }
}
