package com.example.footstepcounter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CompletableDeferred

class SensorReader {

    val isListening: Boolean
        get() = _sensorManager != null && _sensorListener != null

    private var _sensorManager: SensorManager? = null
    private var _stepDetector: Sensor? = null
    private var _sensorListener: SensorEventListener? = null

    private val _permission = Manifest.permission.ACTIVITY_RECOGNITION
    private var _permissionCompletion: CompletableDeferred<Boolean>? = null
    private var _requestCode = 0

    suspend fun askPermission(activity: Activity): Boolean {
        if (isPermissionsGranted(activity)) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(_permission),
                ++_requestCode
            )
            val task = CompletableDeferred<Boolean>()
            _permissionCompletion = task
            val granted = task.await()
            if (!granted) {
                Log.e(TAG, "Permission denied")
            }
            return granted
        }
    }

    fun listen(activity: Activity, onStepCount: (StepCount) -> Unit) {
        if (!isPermissionsGranted(activity)) {
            return
        }

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

    fun onPermissionsGranted(activity: Activity, requestCode: Int) {
        if (requestCode == _requestCode) {
            if (isPermissionsGranted(activity)) {
                _permissionCompletion?.complete(true)
            } else {
                _permissionCompletion?.complete(false)
            }
        }
    }

    private fun isPermissionsGranted(activity: Activity): Boolean {
        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            val grantedPermissions = ActivityCompat.checkSelfPermission(activity, _permission)
            return grantedPermissions == PackageManager.PERMISSION_GRANTED
        }
        return true
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
