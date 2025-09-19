import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CoffeeMaker
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tomatonotpotato.R
import com.example.tomatonotpotato.ui.components.SettingItem
import com.example.tomatonotpotato.ui.components.SettingsColumn
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AboutPage(onBack: () -> Unit = {}) {
    val appVersion = "v1.0.0"
    val context = LocalContext.current

    val settingsItems: List<SettingItem> = listOf(
        SettingItem.InfoSetting(
            title = "App version",
            description = appVersion,
            icon = {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "App version $appVersion",
                )
            }
        ),
        SettingItem.NavigationSetting(
            title = "Rate this app",
            description = "Enjoy the app? Please rate it on the Play Store!",
            onClick = {
                val packageName = context.packageName
                try {
                    // Try to open the Play Store app directly
                    val intent = Intent(Intent.ACTION_VIEW,
                        "market://details?id=$packageName".toUri())
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // If Play Store is not installed, open the web link
                    val intent = Intent(Intent.ACTION_VIEW,
                        "https://play.google.com/store/apps/details?id=$packageName".toUri())
                    context.startActivity(intent)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.StarRate,
                    contentDescription = "Reset settings",
                )
            }
        ),
        SettingItem.NavigationSetting(
            title = "Source code",
            description = "View on GitHub",
            onClick = {
                val url = "https://github.com/johnzhou1210/tomato-not-potato"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "Reset settings",
                )
            }
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.app_icon_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
            )
            Text(
                "Tomato, Not Potato",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                "A practical Pomodoro timer.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            SettingsColumn(settingsItems)
        }

    }

}



