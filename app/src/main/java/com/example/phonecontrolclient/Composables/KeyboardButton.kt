package com.example.phonecontrolclient.Composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.phonecontrolclient.R

@Composable
fun KeyboardIcon(keyPressed: (x: String) -> Unit = { _ -> }) {
    var textValue = remember { mutableStateOf("") }
    val backgroundImage = ImageBitmap.imageResource(id = R.drawable.keyboard)

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        BasicTextField(
            value = textValue.value,
            onValueChange = { message: String ->
                if (message != "") keyPressed(message)
            },
            modifier = Modifier
                .onKeyEvent { keyEvent ->

                    
                    when (keyEvent.key) {
                        Key.Enter -> {
                            keyPressed("ENTER")
                            true
                        }
                        Key.Backspace -> {
                            keyPressed("DELETE")
                            true
                        }
                        else -> false
                    }
                }
        )
    }
}
