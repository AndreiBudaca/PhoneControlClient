package com.example.phonecontrolclient.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.phonecontrolclient.NetworkInteraction.ControlEvent
import com.example.phonecontrolclient.NetworkInteraction.ControlEventType
import com.example.phonecontrolclient.NetworkInteraction.Network
import com.example.phonecontrolclient.NetworkInteraction.SpecialEventTargets
import java.net.Socket

@Composable
fun PhoneControlClient() {
    var socket by remember { mutableStateOf<Socket?>(null) }
    var ipAddr by remember { mutableStateOf(TextFieldValue("192.168.1.133")) }
    var errorMessage by remember { mutableStateOf("") }
    var networkConnection by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Network.connectToServer(socket, ipAddr.text) {
            if (it != null) {
                socket = it
                networkConnection = true
            } else {
                errorMessage = "Failed to connect to server"
                networkConnection = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            socket?.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        Box(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .background(color = if (networkConnection) Color.Green else Color.Red)
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = ipAddr,
            onValueChange = { ipAddr = it },
            label = { Text("Ip address") },
        )

        Text(
            text = errorMessage
        )

        Button(
            onClick = {
                // Trigger connection to server
                Network.connectToServer(socket, ipAddr.text) {
                    if (it != null) {
                        socket = it
                        networkConnection = true
                    } else {
                        errorMessage = "Failed to connect to server"
                        networkConnection = false
                    }
                }
            },
        ) {
            Text("Reconnect")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TrackPad(displayInfo = true, moved = { x, y ->
            Network.sendMessage(
                socket, ControlEvent(ControlEventType.Mouse, x.toString(), y.toString())
            ) {
                networkConnection = it
            }
        }, onRightClick = {
            Network.sendMessage(
                socket, ControlEvent(ControlEventType.MouseButton, "-1", null)
            ) {
                networkConnection = it
            }
        }, onLeftClick = {
            Network.sendMessage(
                socket, ControlEvent(ControlEventType.MouseButton, "1", null)
            ) {
                networkConnection = it
            }
        })

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
        ) {
            JoyStick(size = 100.dp, dotSize = 20.dp) { x, y ->
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.MouseWheel, y.toString(), null)
                ) {
                    networkConnection = it
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            Network.sendMessage(
                                socket, ControlEvent(
                                    ControlEventType.Specials,
                                    SpecialEventTargets.ApplicationWindow.toString(),
                                    "left"
                                )
                            ) {
                                networkConnection = it
                            }
                        },
                    ) { Text("<--", fontSize = TextUnit(value = 5f, type = TextUnitType.Em)) }
                    Button(onClick = {
                        Network.sendMessage(
                            socket,
                            ControlEvent(
                                ControlEventType.Specials,
                                SpecialEventTargets.ApplicationWindow.value,
                                "right"
                            )
                        ) {
                            networkConnection = it
                        }
                    }) {
                        Text(
                            "-->",
                            fontSize = TextUnit(value = 5f, type = TextUnitType.Em)
                        )
                    }
                }
                KeyboardIcon {
                    Network.sendMessage(
                        socket, ControlEvent(ControlEventType.Keyboard, it, null)
                    ) { success ->
                        networkConnection = success
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}