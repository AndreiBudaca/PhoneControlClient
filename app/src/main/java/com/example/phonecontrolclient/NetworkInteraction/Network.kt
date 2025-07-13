package com.example.phonecontrolclient.NetworkInteraction

import android.util.Log
import com.example.phonecontrolclient.NetworkInteraction.IPFinder.IPFinder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class Network {
    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        fun connect(ipAddress: String, onResponse: (NetworkEvent, Socket?) -> Unit) {
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    onResponse(NetworkEvent.EstablishingConnection, null)
                }

                val socket = connectToIP(ipAddress)
                val event =  if (socket != null) NetworkEvent.Connected else NetworkEvent.ConnectionFailed

                withContext(Dispatchers.Main) {
                    onResponse(event, socket)
                }
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun connect(finders: List<IPFinder>, onResponse: (NetworkEvent, String, Socket?) -> Unit) {
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    onResponse(NetworkEvent.EstablishingConnection, "", null)
                }

                var connectionFound = false

                for (finder in finders) {
                    val ips = finder.findIPs() ?: continue

                    for (ip in ips) {
                        withContext(Dispatchers.Main) {
                            onResponse(NetworkEvent.TryingIp, ip, null)
                        }

                        val socket = connectToIP(ip) ?: continue

                        withContext(Dispatchers.Main) {
                            onResponse(NetworkEvent.Connected, ip, socket)
                        }

                        connectionFound = true
                        break
                    }

                    if (connectionFound) break
                }

                if (!connectionFound) {
                    withContext(Dispatchers.Main) {
                        onResponse(NetworkEvent.ConnectionFailed, "", null)
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

        private fun connectToIP(ipAddress: String): Socket? {
            try {
                val socket = Socket()

                socket.connect(
                    InetSocketAddress(ipAddress, 34999),
                    1000)

                return socket
            } catch (e: Exception) {
                Log.e("TCPClient", "Error: ${e.message}")
                return null
            }
        }
    }
}