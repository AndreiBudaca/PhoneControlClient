package com.example.phonecontrolclient.Composables

import Touchpad
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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

@Composable
fun TrackPad(
    displayInfo: Boolean = false,
    scrolled: (y: Float) -> Unit = {_ -> },
    moved: (x: Float, y: Float) -> Unit = { _, _ -> },
    onLeftClick: () -> Unit = { },
    onRightClick: () -> Unit = { },
) {
    var sensitivity by remember { mutableFloatStateOf(0.5f) }
    var infoText by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Sensitivity")
        Slider(
            modifier = Modifier.fillMaxWidth(.5f),
            value = sensitivity,
            onValueChange = { sensitivity = it }
        )

        Row {
            Touchpad(
                width = 50.dp,
                height = 250.dp,
                moved = { _, y ->
                    scrolled(3 * sensitivity * y)
                },
                backgroundColor = Color.Cyan
            )

            Spacer(modifier = Modifier.width(10.dp))

            Touchpad(
                width = 250.dp,
                height = 250.dp,
                moved = { x, y ->
                    infoText = "$x $y"
                    moved(3 * sensitivity * x, 3 * sensitivity * y)
                },
                onTap = { onLeftClick() }
            )
        }
        Spacer(modifier = Modifier.height(25.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier
                    .width(150.dp)
                    .height(30.dp)
                    .background(color = Color.Cyan),
                onClick = {
                    onLeftClick()
                },
            ) { Box(Modifier.background(color = Color.Cyan)) {} }

            Surface(
                modifier = Modifier
                    .width(150.dp)
                    .height(30.dp)
                    .background(color = Color.Cyan),
                onClick = {
                    onRightClick()
                },
            ) { Box(Modifier.background(color = Color.Cyan)) {} }
        }

        if (displayInfo) {
            Text(infoText)
        }
    }
}