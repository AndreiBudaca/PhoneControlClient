package com.example.phonecontrolclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.phonecontrolclient.Composables.PhoneControlClient
import com.example.phonecontrolclient.ui.theme.PhoneControlClientTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoneControlClientTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhoneControlClient()
                }
            }
        }
    }
}

