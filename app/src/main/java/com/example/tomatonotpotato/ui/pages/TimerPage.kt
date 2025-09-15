package com.example.tomatonotpotato.ui.pages

import com.example.tomatonotpotato.R // Replace with your actual package name
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatonotpotato.ui.Dot
import com.example.tomatonotpotato.data.BreakType
import com.example.tomatonotpotato.data.PomodoroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun TimerPage(viewModel: PomodoroViewModel) {
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
            text = when (state.breakType) {
                BreakType.SHORT_BREAK -> "Short Break"
                BreakType.LONG_BREAK -> "Long Break"
                else -> "Focus"
            },
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formattedTime,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayLarge
        )

        Row {
            for (phase in 1..4) {
                Dot(
                    color = if (state.currentPhase >= phase) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.surfaceDim,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(360.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp
            )

            Image(
                painter = if (state.breakType != BreakType.NONE) painterResource(id = R.drawable.potato_small) else painterResource(
                    id = R.drawable.tomato
                ),
                contentDescription = null,
                modifier = Modifier.size(400.dp)
            )


        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Button(
//                modifier = Modifier.padding(8.dp),
//                onClick = { viewModel.resetTimer() },
//                shape = RoundedCornerShape(20.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//            ) {
//                Text("Reset", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onError)
//            }


//            Button(
//                modifier = Modifier.padding(8.dp),
//                onClick = { viewModel.toggleTimer() },
//                shape = RoundedCornerShape(20.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (state.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
//                )
//            ) {
//                Text(
//                    text = if (state.isRunning) "Pause" else "Start",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//            }

            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.size(42.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Filled.Replay,
                    contentDescription = "Reset Pomodoro Timer",
                    modifier = Modifier.fillMaxSize(),

                    )
            }

            IconButton(onClick = { viewModel.toggleTimer() }, modifier = Modifier.size(96.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = if (!state.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)) {
                Icon(
                    imageVector = if (state.isRunning) Icons.Filled.PauseCircleOutline else Icons.Filled.PlayCircleOutline,
                    contentDescription = if (state.isRunning) "Pause Pomodoro Timer" else "Start Pomodoro Timer",
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = { viewModel.advanceToNextPhase() },
                modifier = Modifier.size(42.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next Phase",
                    modifier = Modifier.fillMaxSize()
                )
            }


        }
    }
}