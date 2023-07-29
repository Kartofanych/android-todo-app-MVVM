package com.example.todoapp.ui.view.manageTaskCompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.todoapp.theme.Typography
import com.example.todoapp.theme.YandexTodoTheme
import java.sql.Date
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerSwitchable(
    showTimePicker: MutableState<Boolean>,
    dateChanged: MutableState<Boolean>,
    selectedDate: Date?,
    onTimeSelected: (Date) -> Unit
) {
    val state = rememberTimePickerState()
    val showingPicker = remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    if (showTimePicker.value) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = YandexTodoTheme.colors.backPrimary,
                secondary = YandexTodoTheme.colors.labelPrimary,
                primary = Color(0x4D007AFF),
                onPrimary = YandexTodoTheme.colors.colorBlue,
                surfaceTint = YandexTodoTheme.colors.labelPrimary,
                tertiary = YandexTodoTheme.colors.labelPrimary,
                onSecondary = YandexTodoTheme.colors.labelPrimary,
                background = YandexTodoTheme.colors.backPrimary,)
        ) {
            TimePickerDialog(

                onCancel = {
                    showTimePicker.value = false
                    dateChanged.value = false
                },
                onConfirm = {
                    val cal = Calendar.getInstance()
                    if (selectedDate != null) {
                        cal.time = selectedDate
                    }
                    cal.set(Calendar.HOUR_OF_DAY, state.hour)
                    cal.set(Calendar.MINUTE, state.minute)
                    cal.isLenient = false
                    onTimeSelected(Date(cal.time.time))
                    showTimePicker.value = false
                },
                toggle = {
                    if (configuration.screenHeightDp > 400) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .semantics {
                                    isContainer = true
                                }.background(YandexTodoTheme.colors.backPrimary),
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .size(64.dp, 72.dp)
                                    .align(Alignment.BottomStart)
                                    .zIndex(5f),
                                onClick = { showingPicker.value = !showingPicker.value }) {
                                val icon = if (showingPicker.value) {
                                    Icons.Outlined.KeyboardArrowUp
                                } else {
                                    Icons.Outlined.KeyboardArrowDown
                                }
                                Icon(
                                    icon,
                                    contentDescription = if (showingPicker.value) {
                                        "Switch to Text Input"
                                    } else {
                                        "Switch to Touch Input"
                                    }
                                )
                            }
                        }
                    }
                }
            ) {
                if (showingPicker.value && configuration.screenHeightDp > 400) {
                    TimePicker(
                        state = state,
                        modifier = Modifier.
                            background(YandexTodoTheme.colors.backPrimary),
                        colors = TimePickerDefaults.colors(
                            clockDialColor = YandexTodoTheme.colors.backElevated,
                            clockDialSelectedContentColor = YandexTodoTheme.colors.colorBlue,
                            timeSelectorSelectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            timeSelectorUnselectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            timeSelectorSelectedContainerColor = YandexTodoTheme.colors.backSecondary,
                            timeSelectorUnselectedContainerColor = YandexTodoTheme.colors.backPrimary,
                            periodSelectorSelectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            periodSelectorUnselectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            containerColor = YandexTodoTheme.colors.backPrimary,
                            periodSelectorBorderColor = YandexTodoTheme.colors.colorBlue,
                            periodSelectorSelectedContainerColor = YandexTodoTheme.colors.backPrimary,
                            clockDialUnselectedContentColor = YandexTodoTheme.colors.labelPrimary
                        ),
                    )
                } else {
                    TimeInput(
                        state = state,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0x4D007AFF),
                            clockDialSelectedContentColor = YandexTodoTheme.colors.colorBlue,
                            timeSelectorSelectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            timeSelectorUnselectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            timeSelectorSelectedContainerColor = YandexTodoTheme.colors.colorBlue,
                            timeSelectorUnselectedContainerColor = YandexTodoTheme.colors.backSecondary,
                            periodSelectorSelectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            periodSelectorUnselectedContentColor = YandexTodoTheme.colors.labelPrimary,
                            containerColor = YandexTodoTheme.colors.backPrimary,
                            periodSelectorBorderColor = YandexTodoTheme.colors.colorBlue,
                            periodSelectorSelectedContainerColor = YandexTodoTheme.colors.backPrimary,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)

        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Установите время",
                    style = Typography.body2,
                    color = YandexTodoTheme.colors.labelPrimary
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Отмена",
                        style = Typography.body2,
                        color = YandexTodoTheme.colors.labelTertiary,
                    ) }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("Установить",
                        style = Typography.body2,
                        color = YandexTodoTheme.colors.colorBlue
                    ) }
                }
            }
        }
    }
}
