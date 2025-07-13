package com.example.phonecontrolclient.NetworkInteraction.IPFinder

import android.content.Context

class FileFinder(private val context: Context): IPFinder {
    override fun findIPs(): List<String> {
        context.openFileOutput("ips.file", Context.MODE_PRIVATE).use {  }
        context.openFileInput("ips.file").use {
            val content = String(it.readAllBytes())
            return content.lines().filter { x -> x.isNotEmpty() }
        }
    }
}