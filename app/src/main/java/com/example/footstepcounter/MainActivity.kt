package com.example.footstepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.footstepcounter.ui.theme.FootStepCounterTheme

class MainActivity : ComponentActivity() {
    private val _sensorReader: SensorReader = SensorReader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var stepCount by remember {
                mutableIntStateOf(0)
            }
            var isListening by remember {
                mutableStateOf(true)
            }
            _sensorReader.listen(this) {
                stepCount = it.totalStep
            }
            val toggleSensorReader = {
                if (_sensorReader.isListening) {
                    _sensorReader.removeListener()
                    stepCount = 0
                } else {
                    _sensorReader.listen(this) {
                        stepCount = it.totalStep
                    }
                    stepCount = 0
                }
                isListening = _sensorReader.isListening
            }

            FootStepCounterTheme {
                App(
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
}