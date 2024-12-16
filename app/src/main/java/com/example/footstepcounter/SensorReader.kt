package com.example.footstepcounter

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorReader {

    val isListening: Boolean
        get() = _sensorManager != null && _sensorListener != null

    private var _sensorManager: SensorManager? = null
    private var _stepDetector: Sensor? = null
    private var _sensorListener: SensorEventListener? = null

    fun listen(activity: Activity, onStepCount: (StepCount) -> Unit) {
        removeListener()

        if (_sensorManager == null) {
            _sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        if (_stepDetector == null) {
            _stepDetector = _sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        }
        if (_stepDetector != null) {
            _sensorListener = SensorEventListener(onStepCount)
            _sensorManager!!.registerListener(
                _sensorListener,
                _stepDetector,
                SensorManager.SENSOR_DELAY_UI
            )
            Log.d(TAG, "Listening to step detector sensor")
        } else {
            Log.e(TAG, "Step detector sensor not available")
        }
    }

    fun removeListener() {
        if (isListening) {
            Log.d(TAG, "Removing sensor listener")
            _sensorManager?.unregisterListener(_sensorListener)
            _sensorListener = null
        }
    }

    companion object {
        private const val TAG = "SensorReader"
    }
}

class SensorEventListener(
    private val _onStepCount: (StepCount) -> Unit
) : SensorEventListener {
    private var _stepCount = 0
    private var _record = true

    override fun onSensorChanged(ev: SensorEvent?) {
        if (ev?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            if (_record) {
                _stepCount++
                _onStepCount(StepCount(_stepCount, 1))
            }
        }
    }

    override fun onAccuracyChanged(ev: Sensor?, accuracy: Int) {
        if (ev?.type == Sensor.TYPE_STEP_DETECTOR) {
            _record = accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH
        }
    }
}

data class StepCount(val totalStep: Int, val changed: Int)
