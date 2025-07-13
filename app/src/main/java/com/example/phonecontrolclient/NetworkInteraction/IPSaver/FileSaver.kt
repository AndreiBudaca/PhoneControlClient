package com.example.phonecontrolclient.NetworkInteraction.IPSaver

import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FileSaver(private val context: Context): IPSaver {
    @OptIn(DelicateCoroutinesApi::class)
    override fun saveIP(ip: String) {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            var lines: List<String>

            // Read existing text
            context.openFileInput("ips.file").use { input ->
                lines = String(input.readAllBytes()).lines()
            }

            context.openFileOutput("ips.file", Context.MODE_PRIVATE).use { output ->
                val newList = listOf(ip) + lines.filter { x -> x.isNotEmpty() }.take(10)
                output.write(newList.joinToString("\r\n").toByteArray())
                output.flush()
            }
        }
    }
}