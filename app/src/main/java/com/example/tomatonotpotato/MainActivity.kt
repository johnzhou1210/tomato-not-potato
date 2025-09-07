package com.example.tomatonotpotato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- Data Model ---
data class PomodoroState(
    val totalTimeMillis: Long,
    val timeLeftMillis: Long,
    val isRunning: Boolean,
    val isBreak: Boolean
)

// --- ViewModel ---
class PomodoroViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        PomodoroState(
            totalTimeMillis = 25 * 60 * 1000L,
            timeLeftMillis = 25 * 60 * 1000L,
            isRunning = false,
            isBreak = false
        )
    )
    val state = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setTimer(mins: Int, isRunning: Boolean, isBreak: Boolean) {
        _state.value = PomodoroState(
            totalTimeMillis = mins * 60 * 1000L,
            timeLeftMillis = mins * 60 * 1000L,
            isRunning = isRunning,
            isBreak = isBreak
        )
    }

    fun toggleTimer() {
        if (_state.value.isRunning) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        _state.value = _state.value.copy(isRunning = true)

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeftMillis > 0 && _state.value.isRunning) {
                delay(1000)
                _state.value = _state.value.copy(
                    timeLeftMillis = _state.value.timeLeftMillis - 1000
                )
            }
            if (_state.value.timeLeftMillis <= 0) onTimerFinished()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        setTimer(25, false, false)
    }

    private fun onTimerFinished() {
        if (!_state.value.isBreak) {
            setTimer(5, true, true)
        } else {
            resetTimer()
        }
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}



// --- Composable ---
@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel = viewModel()) {
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

// --- MainActivity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroScreen()
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun PomodoroScreenPreview() {
    PomodoroScreen()
}
