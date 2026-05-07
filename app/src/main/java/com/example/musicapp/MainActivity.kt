package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.musicapp.navigation.AppNavigation
import com.example.musicapp.ui.theme.MusicAppTheme
import com.example.musicapp.ui.theme.PurpleBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PurpleBg)
                ) { _ ->
                    AppNavigation()
                }
            }
        }
    }
}
