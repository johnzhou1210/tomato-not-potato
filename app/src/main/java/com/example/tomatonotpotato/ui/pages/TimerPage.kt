package com.example.tomatonotpotato.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.viewmodels.PomodoroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Composable ---
@Composable
fun TimerPage(viewModel: PomodoroViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
        Date(state.timeLeftMillis)
    )

    val progress = state.timeLeftMillis.toFloat() / state.totalTimeMillis

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formattedTime,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(200.dp),
                color = if (state.isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                strokeWidth = 10.dp
            )

            Text(
                text = if (state.isBreak) "Break" else "Focus",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.toggleTimer() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isRunning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (state.isRunning) "Pause" else "Start",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = { viewModel.resetTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reset", color = MaterialTheme.colorScheme.onError)
            }

        }
    }
}