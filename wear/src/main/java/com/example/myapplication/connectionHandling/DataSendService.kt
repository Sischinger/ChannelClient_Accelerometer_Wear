import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.google.android.gms.wearable.ChannelIOException
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer

class DataSendService(private val context: Context, private val outputStream: OutputStream) : Thread(), SensorEventListener {

    private val TAG = "DataSendService"
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    override fun run() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    private fun sendData(outputStream: OutputStream, data: FloatArray): Boolean {
        return try {
            // Create a ByteBuffer and put each float value into it
            val buffer = ByteBuffer.allocate(data.size * 4)
            for (f in data) {
                buffer.putFloat(f)
            }

            // Send the ByteBuffer as a byte array
            outputStream.write(buffer.array())
            outputStream.flush()

            Log.d(TAG, "Data sent: ${data.contentToString()}")
            true
        } catch (e: IOException) {
            if (e is ChannelIOException) {
                Log.d(TAG, "Channel closed properly")
            } else {
                Log.e(TAG, "Error writing to outputStream: $e")
            }
            false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Get the accelerometer values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Put the values into an array of floats
            val data = floatArrayOf(x, y, z)

            // Send the data as floats
            sendData(outputStream, data)
        }
    }

    fun unregisterListener() {
        accelerometer?.let {
            sensorManager?.unregisterListener(this, it)
        }
    }
}
