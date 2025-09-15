package com.example.tomatonotpotato.ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val FocusColors = lightColorScheme(
    primary = Color(0xFFEF5350), // Tomato red
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5), // Light gray
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E0E0), // Slightly darker gray for a subtle contrast
    onSurfaceVariant = Color(0xFF757575), // A mid-tone gray for text/icons
    secondary = Color(0xFF69A16A),
    inversePrimary = Color(0xFF8F3130)
)

val BreakColors = lightColorScheme(
    primary = Color(0xFFD98A3B), // Potato orange
    onPrimary = Color.White,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E0E0), // Consistent across both themes
    onSurfaceVariant = Color(0xFF757575), // Consistent across both themes
    secondary =  Color(0xFFFFB74D),// A darker orange
    inversePrimary = Color(0xFF825223)
)




val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)