import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SettingsPage() {
    // 1. Define a list of SettingItem objects, not just strings.
    val settingsItems: List<SettingItem> = listOf(
        SettingItem.SwitchSetting(
            title = "Dark mode",
            description = "Enable or disable dark mode",
            isChecked = false,
            onCheckedChange = {}
        ),
        SettingItem.SwitchSetting(
            title = "Notifications",
            description = "Control what notifications you get",
            isChecked = true,
            onCheckedChange = {}
        ),
        SettingItem.NumberSetting(
            title = "Focus duration",
            description = "Set the duration of a focus session",
            value = 25,
            onValueChange = {}
        ),
        SettingItem.NumberSetting(
            title = "Break duration",
            description = "Set the duration of a short break",
            value = 5,
            onValueChange = {}
        ),
        SettingItem.NumberSetting(
            title = "Long break duration",
            description = "Set the duration of a long break",
            value = 30,
            onValueChange = {}
        ),
        SettingItem.NavigationSetting(
            title = "About",
            description = "Learn more about the app",
            onClick = {}
        )
    )

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))
        LazyColumn {
            // 2. The `items` function now correctly iterates over your sealed class list.
            items(settingsItems) { item ->
                // 3. Use a `when` statement to render the correct composable for each type.
                when (item) {
                    is SettingItem.SwitchSetting -> {
                        SwitchItem(
                            title = item.title,
                            description = item.description,
                            isChecked = item.isChecked,
                            onCheckedChange = item.onCheckedChange
                        )
                    }
                    is SettingItem.NumberSetting -> {
                        NumberItem(
                            title = item.title,
                            description = item.description,
                            value = item.value,
                            onValueChange = item.onValueChange
                        )
                    }
                    is SettingItem.NavigationSetting -> {
                        NavigationItem(
                            title = item.title,
                            description = item.description,
                            onClick = item.onClick
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SwitchItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}


@Composable
fun NumberItem(
    title: String,
    description: String,
    value: Number,
    onValueChange: (Int) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
    )
}

@Composable
fun NavigationItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
    )
}




sealed class SettingItem(
    open val title: String,
    open val description: String
) {
    data class SwitchSetting(
        override val title: String,
        override val description: String,
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingItem(title, description)

    data class NumberSetting(
        override val title: String,
        override val description: String,
        val value: Int,
        val onValueChange: (Int) -> Unit
    ) : SettingItem(title, description)

    data class NavigationSetting(
        override val title: String,
        override val description: String,
        val onClick: () -> Unit
    ) : SettingItem(title, description)
}