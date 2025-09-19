package com.johnzhou.tomatonotpotato.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.johnzhou.tomatonotpotato.ui.theme.ErrorColor

sealed class SettingItem(
    open val title: String,
    open val description: String,
    open val icon: @Composable () -> Unit = {}
) {
    data class SwitchSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        val enabled: Boolean = true
    ) : SettingItem(title, description)

    data class NumberSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val unitSingular: String,
        val unitPlural: String,
        val value: Int,
        val minVal: Int = 0,
        val maxVal: Int = 999,
        val onValueChange: (Int) -> Unit
    ) : SettingItem(title, description)

    data class NavigationSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val onClick: () -> Unit
    ) : SettingItem(title, description)

    data class ResetSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
        val onReset: () -> Unit,
    ) : SettingItem(title, description)

    data class InfoSetting(
        override val title: String,
        override val description: String,
        override val icon: @Composable () -> Unit = {},
    ) : SettingItem(title, description)
}

@Composable
fun SwitchItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: @Composable () -> Unit = {},
    enabled: Boolean = true
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        trailingContent = {
            Switch(
                enabled = enabled,
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.clickable { if (enabled) onCheckedChange(!isChecked) }
    )
}


@Composable
fun NumberItem(
    title: String,
    value: Int,
    unitSingular: String = "",
    unitPlural: String = "",
    onValueChange: (Int) -> Unit,
    icon: @Composable () -> Unit = {},
    description: String,
    minVal: Int = 0,
    maxVal: Int = 999
) {
    var showDialog by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text("$value ${if (value == 1) unitSingular else unitPlural}") },
        leadingContent = icon,
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        NumberInputDialog(
            title = description,
            currentValue = value,
            units = unitPlural,
            onDismiss = { showDialog = false },
            onConfirm = { newValue ->
                onValueChange(newValue)
                showDialog = false
            },
            minVal = minVal,
            maxVal = maxVal
        )
    }

}

@Composable
fun NavigationItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to $title",
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetItem(
    title: String,
    description: String,
    onReset: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(title, color = ErrorColor) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        modifier = Modifier.clickable { showDialog = true }
    )

    // Alert dialog goes here
    if (showDialog) {
        ResetDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                onReset()
                showDialog = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoItem(
    title: String,
    description: String,
    icon: @Composable () -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(title, color = MaterialTheme.colorScheme.onSurface) },
        supportingContent = { Text(description) },
        leadingContent = icon,
        modifier = Modifier.clickable {}
    )

}


@Composable
fun NumberInputDialog(
    title: String,
    currentValue: Int,
    units: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    minVal: Int,
    maxVal: Int,
) {
    var inputValue by remember { mutableStateOf(TextFieldValue(currentValue.toString())) }
    var isInputValid = remember(inputValue) {
        val number = inputValue.text.toIntOrNull()
        number != null && number in minVal..maxVal
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val indicatorColor by animateColorAsState(
        targetValue = if (isInputValid) MaterialTheme.colorScheme.secondary else ErrorColor,
        label = "indicatorColorAnimation"
    )
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        text = {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { newValue ->
                    val number = newValue.text.toIntOrNull()
                    if (newValue.text.isEmpty() || (number != null && number in minVal..maxVal)) {
                        inputValue = newValue
                    }
                },
                label = { Text("$title ($units)", style = MaterialTheme.typography.labelSmall) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = indicatorColor,
                    focusedIndicatorColor = indicatorColor,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val newValue = inputValue.text.toIntOrNull()
                    if (newValue != null) {
                        onConfirm(newValue)
                    }
                },
                enabled = isInputValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "OK",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor,
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    )
    // Request focus when the dialog is first composed
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
        inputValue = inputValue.copy(selection = TextRange(inputValue.text.length))
    }
}


@Preview
@Composable
fun ResetDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Are you sure?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onDismissRequest = onDismiss,
        text = {
            Text(
                "Your Pomodoro history will not be deleted.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor,
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "Yes",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "No",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    )

}


@Composable
fun SettingsColumn(settingsItems: List<SettingItem>, modifier: Modifier = Modifier) {
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
                        onCheckedChange = item.onCheckedChange,
                        icon = item.icon,
                        enabled = item.enabled
                    )
                }

                is SettingItem.NumberSetting -> {
                    NumberItem(
                        title = item.title,
                        value = item.value,
                        onValueChange = item.onValueChange,
                        unitSingular = item.unitSingular,
                        unitPlural = item.unitPlural,
                        description = item.description,
                        minVal = item.minVal,
                        maxVal = item.maxVal,
                        icon = item.icon
                    )
                }

                is SettingItem.NavigationSetting -> {
                    NavigationItem(
                        title = item.title,
                        description = item.description,
                        onClick = item.onClick,
                        icon = item.icon
                    )
                }

                is SettingItem.ResetSetting -> {
                    ResetItem(
                        title = item.title,
                        description = item.description,
                        onReset = item.onReset,
                        icon = item.icon
                    )
                }

                is SettingItem.InfoSetting -> {
                    InfoItem(
                        title = item.title,
                        description = item.description,
                        icon = item.icon
                    )
                }
            }
        }
    }
}