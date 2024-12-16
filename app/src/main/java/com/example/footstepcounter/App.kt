package com.example.footstepcounter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.footstepcounter.ui.theme.FootStepCounterTheme

@Composable
fun App(
    permissionGranted: Boolean,
    stepCount: Int,
    isListening: Boolean,
    toggleSensorReader: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Content(if (permissionGranted) "Step Count: $stepCount" else "We need your permission")
        Button(
            onClick = toggleSensorReader,
        ) {
            Text(text = if (permissionGranted && isListening) "Stop" else "Start")
        }
    }

}

@Composable
fun Content(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            modifier = modifier
        )
    }

}

@Preview(showBackground = true, widthDp = 320, heightDp = 240)
@Composable
fun AppPreview() {
    FootStepCounterTheme {
        App(
            false,
            1,
            true,
            {},
            Modifier.fillMaxSize()
        )
    }
}