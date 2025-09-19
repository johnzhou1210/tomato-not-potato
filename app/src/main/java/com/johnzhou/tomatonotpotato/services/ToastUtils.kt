package com.johnzhou.tomatonotpotato.services

import android.content.Context
import android.widget.Toast


private var lastToastTimestampMs: Long = 0


fun showToastWithCooldown(context: Context, message: String, duration: Long = 2000L, longLengthToast : Boolean = false) {
    val currentTimeMs = System.currentTimeMillis()
    if (currentTimeMs - lastToastTimestampMs > duration) {
        Toast.makeText(context, message, if (longLengthToast) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
        lastToastTimestampMs = currentTimeMs
    }
}