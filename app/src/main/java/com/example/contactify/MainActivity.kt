package com.example.contactify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.contactify.navigation.AppNavigation
import com.example.contactify.ui.theme.ContactifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ContactifyTheme {
                AppNavigation()
            }
        }
    }
}