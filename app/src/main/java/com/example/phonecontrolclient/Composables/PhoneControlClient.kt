package com.example.phonecontrolclient.Composables

import ServerInfoOverlay
import ToggleOverlayButton
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    var ipAddr by remember { mutableStateOf(TextFieldValue("")) }
    var infoText by remember { mutableStateOf("") }
    var networkConnection by remember { mutableStateOf(false) }
    var searchingNetwork by remember { mutableStateOf(false) }
    var serverInfoOverlay by remember { mutableStateOf(false) }

    fun tryAddresses(addresses: List<String>, index: Int) {
        if (index >= addresses.size) {
            infoText = "Could not connect to server"
            searchingNetwork = false
            return
        }

        val currentAddress = addresses[index]
        ipAddr = TextFieldValue(currentAddress)

        Network.connectToServer(socket, addresses[index]) {
            if (it == null) tryAddresses(addresses, index + 1)
            else {
                socket = it
                networkConnection = true
                infoText = "Connected"
                searchingNetwork = false
            }
        }
    }

    fun connect() {
        if (searchingNetwork) return

        searchingNetwork = true
        if (ipAddr.text.isEmpty()) {
            infoText = "Searching server..."
            Network.discoverService {
                if (it == null) {
                    infoText = "Server not found"
                } else {
                    infoText = "Trying addresses..."
                    tryAddresses(it, 0)
                }
            }
        } else {
            Network.connectToServer(socket, ipAddr.text) {
                searchingNetwork = false
                if (it != null) {
                    socket = it
                    networkConnection = true
                    infoText = "Connected"
                } else {
                    infoText = "Failed to connect to server"
                    networkConnection = false
                }
            }
        }
    }

    fun updateConnection(connectionAlive: Boolean) {
        if (!connectionAlive && socket != null) {
            socket?.close()
            connect()
        }

        if (!connectionAlive) {
            socket = null
        }

        networkConnection = connectionAlive
    }

    LaunchedEffect(Unit) {
        connect()
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
                .background(
                    color = if (networkConnection) Color.Green else if (searchingNetwork) Color.Yellow else Color.Red,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        ) {}

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KeyboardIcon {
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.Keyboard, it, null)
                ) { success ->
                    updateConnection(success)
                }
            }

            Button(
                onClick = {
                    Network.sendMessage(
                        socket, ControlEvent(
                            ControlEventType.Specials,
                            SpecialEventTargets.ApplicationWindow.toString(),
                            "left"
                        )
                    ) {
                        updateConnection(it)
                    }
                },
                modifier = Modifier
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan,
                    contentColor = Color.Black // Ensure icons/text are visible on cyan
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Left"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Window"
                )
            }

            Button(
                onClick = {
                    Network.sendMessage(
                        socket,
                        ControlEvent(
                            ControlEventType.Specials,
                            SpecialEventTargets.ApplicationWindow.value,
                            "right"
                        )
                    ) {
                        updateConnection(it)
                    }
                },
                modifier = Modifier
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan,
                    contentColor = Color.Black // Ensure icons/text are visible on cyan
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Window"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Right"
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        TrackPad(
            scrolled = { y ->
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.MouseWheel, y.toString(), null)
                ) {
                    updateConnection(it)
                }
            },
            moved = { x, y ->
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.Mouse, x.toString(), y.toString())
                ) {
                    updateConnection(it)
                }
            },
            onRightClick = {
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.MouseButton, "-1", null)
                ) {
                    updateConnection(it)
                }
            },
            onLeftClick = {
                Network.sendMessage(
                    socket, ControlEvent(ControlEventType.MouseButton, "1", null)
                ) {
                    updateConnection(it)
                }
            }
        )

        Spacer(modifier = Modifier.height(50.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp), // adjust to avoid overlapping toggle button
        contentAlignment = Alignment.TopCenter
    ) {
        ServerInfoOverlay(
            visible = serverInfoOverlay,
            ipAddr = ipAddr,
            infoText = infoText,
            onIpChange = { ipAddr = it },
            onReconnect = { connect() },
            onDismiss = { serverInfoOverlay = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        ToggleOverlayButton {
            serverInfoOverlay = !serverInfoOverlay
        }
    }
}