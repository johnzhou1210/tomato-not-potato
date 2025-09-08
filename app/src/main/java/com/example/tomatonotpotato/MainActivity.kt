package com.example.tomatonotpotato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tomatonotpotato.ui.TimerApp


// --- MainActivity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TimerApp()
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TimerApp()
}
