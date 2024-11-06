package com.example.phonecontrolclient.NetworkInteraction

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class Network {
    companion object {


        @OptIn(DelicateCoroutinesApi::class)
        fun connectToServer(socket: Socket?, ipAddress: String, onResponse: (Socket?) -> Unit) {
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                try {
                    // Close old socket
                    socket?.close()

                    // Connect to the server
                    val newSocket = Socket()
                    newSocket.connect(
                        InetSocketAddress(ipAddress, 34999),
                        1000)

                    // Pass the response back to the UI thread
                    withContext(Dispatchers.Main) {
                        onResponse(newSocket)
                    }
                } catch (e: Exception) {
                    Log.e("TCPClient", "Error: ${e.message}")
                    withContext(Dispatchers.Main) {
                        onResponse(null)
                    }
                }
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun sendMessage(socket: Socket?, message: ControlEvent, onResponse: (Boolean) -> Unit) {
            if (socket == null) {
                onResponse(false)
                return
            }

            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                    try {
                        synchronized(socket) {
                            val outputStream = socket.getOutputStream()
                            outputStream.write(message.toString().toByteArray())
                        }
                        withContext(Dispatchers.Main) {
                            onResponse(true)
                        }
                    } catch (e: Exception) {
                        Log.e("TCPClient", "Error: ${e.message}")
                        withContext(Dispatchers.Main) {
                            onResponse(false)
                        }
                    }
            }
        }
    }
}