package com.example.tomatonotpotato.data

// --- Data Model ---
data class PomodoroState(
    val totalTimeMillis: Long,
    val timeLeftMillis: Long,
    val isRunning: Boolean,
    val isBreak: Boolean
)