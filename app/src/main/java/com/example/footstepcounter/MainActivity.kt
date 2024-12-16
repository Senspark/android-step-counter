package com.example.footstepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.footstepcounter.ui.theme.FootStepCounterTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val _sensorReader: SensorReader = SensorReader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var permissionGranted by mutableStateOf(true)
        var stepCount by mutableIntStateOf(0)
        var isListening by mutableStateOf(true)
        val act = this@MainActivity

        val startListening = {
            lifecycleScope.launch {
                permissionGranted = _sensorReader.askPermission(act)
                if (permissionGranted) {
                    _sensorReader.listen(act) {
                        stepCount = it.totalStep
                    }
                }
            }
        }

        val toggleSensorReader = {
            if (_sensorReader.isListening) {
                _sensorReader.removeListener()
            } else {
                startListening()
            }
            stepCount = 0
            isListening = _sensorReader.isListening
        }

        startListening()

        setContent {
            FootStepCounterTheme {
                App(
                    permissionGranted = permissionGranted,
                    stepCount = stepCount,
                    isListening = isListening,
                    toggleSensorReader = toggleSensorReader,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _sensorReader.removeListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        _sensorReader.onPermissionsGranted(this, requestCode)
    }
}