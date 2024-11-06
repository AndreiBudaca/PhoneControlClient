package com.example.phonecontrolclient.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.phonecontrolclient.R

@Composable
fun TrackPad(
    displayInfo: Boolean = false,
    moved: (x: Float, y: Float) -> Unit = { _, _ -> },
    onLeftClick: (Unit) -> Unit = {_ -> },
    onRightClick: (Unit) -> Unit = {_ -> },
) {
    var sensivity by remember { mutableFloatStateOf(0.5f) }
    var infoText by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Sensitivity")
        Slider(
            modifier = Modifier.fillMaxWidth(.5f),
            value = sensivity,
            onValueChange = { sensivity = it }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .height(250.dp)
                    .width(30.dp)
                    .background(color = Color.Cyan),
                onClick = {
                    onLeftClick(Unit)
                },
            ) { Box(Modifier.background(color = Color.Cyan)){} }

            JoyStick(size = 250.dp) { x, y ->
                infoText = "$x $y"
                moved(sensivity * x / 5, sensivity * y / 5)
            }

            Surface(
                modifier = Modifier
                    .height(250.dp)
                    .width(30.dp)
                    .background(color = Color.Cyan),
                onClick = {
                    onRightClick(Unit)
                },
            ) { Box(Modifier.background(color = Color.Cyan)){} }
        }

        if (displayInfo) {
            Text(infoText)
        }
    }
}