package com.johnzhou.tomatonotpotato.ui.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.johnzhou.tomatonotpotato.R
import com.johnzhou.tomatonotpotato.R.drawable.tomato_high
import com.johnzhou.tomatonotpotato.data.BreakType
import com.johnzhou.tomatonotpotato.data.PomodoroState
import com.johnzhou.tomatonotpotato.data.PomodoroTimerSettings
import com.johnzhou.tomatonotpotato.data.PomodoroViewModel
import com.johnzhou.tomatonotpotato.ui.Dot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Composable
fun TimerPage(pomodoroViewModel: PomodoroViewModel) {
    val state by pomodoroViewModel.state.collectAsState()
    val pomodoroTimerSettings by pomodoroViewModel.pomodoroTimerSettings.collectAsState()

    BoxWithConstraints {
        // We check if the width is greater than the height (a good proxy for landscape)
        // Or you can use a fixed breakpoint like `maxWidth > 600.dp`
        if (maxWidth > maxHeight) {
            LandscapeLayout(
                state = state,
                pomodoroTimerSettings = pomodoroTimerSettings,
                pomodoroViewModel = pomodoroViewModel
            )
        } else {
            PortraitLayout(
                state = state,
                pomodoroTimerSettings = pomodoroTimerSettings,
                pomodoroViewModel = pomodoroViewModel
            )
        }
    }
}



@Composable
fun PortraitLayout(
    state: PomodoroState,
    pomodoroTimerSettings: PomodoroTimerSettings,
    pomodoroViewModel: PomodoroViewModel
) {
    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
        Date(state.timeLeftMillis)
    )
    val isFinished = state.timeLeftMillis <= 0
    val targetProgress = if (isFinished) 0f else state.timeLeftMillis.toFloat() / state.totalTimeMillis
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500, easing = EaseOutQuad)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = when (state.breakType) {
                BreakType.SHORT_BREAK -> "Short Break"
                BreakType.LONG_BREAK -> "Long Break"
                else -> "Focus"
            },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formattedTime,
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row {
            for (phase in 1..pomodoroTimerSettings.pomodorosBeforeLongBreak) {
                Dot(
                    color = if (state.currentPhase >= phase) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceDim,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                )
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 24.dp)) {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(360.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp
            )
            Image(
                painter = if (state.breakType != BreakType.NONE) painterResource(id = R.drawable.potato_small) else painterResource(id = R.drawable.tomato_high),
                contentDescription = null,
                modifier = Modifier.size(400.dp).offset(if (state.breakType == BreakType.NONE) (-8).dp else 0.dp)
            )
        }

        // The weighted spacer works correctly here in portrait
        Spacer(modifier = Modifier.weight(1f))

        TimerControls(pomodoroViewModel, state.isRunning)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LandscapeLayout(
    state: PomodoroState,
    pomodoroTimerSettings: PomodoroTimerSettings,
    pomodoroViewModel: PomodoroViewModel
) {
    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
        Date(state.timeLeftMillis)
    )
    val isFinished = state.timeLeftMillis <= 0
    val targetProgress = if (isFinished) 0f else state.timeLeftMillis.toFloat() / state.totalTimeMillis
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500, easing = EaseOutQuad)
    )

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Left Side of the Row
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(400.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp
            )
            Image(
                painter = if (state.breakType != BreakType.NONE) painterResource(id = R.drawable.potato_small) else painterResource(id = R.drawable.tomato_high),
                contentDescription = null,
                modifier = Modifier.size(400.dp).offset(if (state.breakType == BreakType.NONE) (-8).dp else 0.dp)
            )
        }

        // Right Side of the Row
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (state.breakType) {
                    BreakType.SHORT_BREAK -> "Short Break"
                    BreakType.LONG_BREAK -> "Long Break"
                    else -> "Focus"
                },
                fontSize = 35.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formattedTime,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                for (phase in 1..pomodoroTimerSettings.pomodorosBeforeLongBreak) {
                    Dot(
                        color = if (state.currentPhase >= phase) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            TimerControls(pomodoroViewModel, state.isRunning)
        }
    }
}

// Extracted controls to a separate composable for reuse
@Composable
fun TimerControls(pomodoroViewModel: PomodoroViewModel, isRunning: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { pomodoroViewModel.resetTimer() },
            modifier = Modifier.size(42.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Filled.Replay,
                contentDescription = "Reset Pomodoro Timer",
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = { pomodoroViewModel.toggleTimer() },
            modifier = Modifier.size(96.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = if (!isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary)
        ) {
            AnimatedPlayPauseIcon(isRunning)
        }

        IconButton(
            onClick = { pomodoroViewModel.advanceToNextPhase() },
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

@Composable
fun AnimatedPlayPauseIcon(isTimerRunning: Boolean) {
    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = isTimerRunning,
        label = "icon animation"
    ) { targetState ->
        if (targetState) {
            RoundedPauseButton(
                imageVector = Icons.Filled.Pause,
                contentDescription = "Pause Pomodoro Timer",
                color = MaterialTheme.colorScheme.inversePrimary,
                playing = isTimerRunning
            )
        } else {
            RoundedPauseButton(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Start Pomodoro Timer",
                color = MaterialTheme.colorScheme.primary,
                playing = isTimerRunning
            )
        }
    }
}

@Composable
fun RoundedPauseButton(contentDescription: String, imageVector: ImageVector, color: Color, playing: Boolean) {
    // Animate the corner radius to create a smooth transition between a square and a circle
    val cornerRadius by animateDpAsState(targetValue = if (playing) 16.dp else 32.dp, label = "cornerRadius")
    val buttonColor = if (playing) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .requiredSize(58.dp)
            .background(
                color = buttonColor,
                shape = RoundedCornerShape(cornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp),
            tint = Color.White
        )
    }
}
