package com.example.tomatonotpotato.ui.pages


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.example.tomatonotpotato.R
import com.example.tomatonotpotato.data.PomodoroRecord
import com.example.tomatonotpotato.ui.theme.BreakColors
import com.example.tomatonotpotato.ui.theme.FocusColors
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState


import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val daySize = 18.dp

@Composable
fun HistoryPage(viewModel: PomodoroViewModel = viewModel()) {
//    val today = remember { LocalDate.now() }
    val today = PomodoroViewModel.TODAY
    // Ensure range covers 2 weeks before and after today
    val startMonth = remember { YearMonth.from(today.minusWeeks(2)) }
    val endMonth = remember { YearMonth.from(today.plusWeeks(2)) }

    val currentDaySelection = remember { mutableStateOf(today) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(today),
    )

    val oldestDate by viewModel.oldestDate.collectAsState()

    PomodoroCalendar(
        state = state,
        viewModel = viewModel,
        today = today,
        oldestDate = oldestDate,
        currentDaySelection = currentDaySelection
    )
}

fun getStreak(history: Map<LocalDate, PomodoroRecord>, today: LocalDate): Int {
    var result = 0
    var currDay = when {
        (history[today]?.completedSessions ?: 0) > 0 -> today
        else -> today.minusDays(1)
    }
    while ((history[currDay]?.completedSessions ?: 0) > 0) {
        result++
        currDay = currDay.minusDays(1)
    }
    return result
}


@Composable
fun PomodoroCalendar(
    viewModel: PomodoroViewModel,
    state: CalendarState,
    today: LocalDate,
    oldestDate: LocalDate?,
    currentDaySelection: MutableState<LocalDate>
) {
    val records by viewModel.records.collectAsState()
    val totalPomodori by viewModel.totalPomodori.collectAsState()
    val history = remember(records) {
        records.associateBy { it.date }
    }

    val currStreak = getStreak(history, today)
    viewModel.updateBestStreak(currStreak)

    Column(
        modifier = Modifier
            .padding(all = 32.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "History", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(128.dp))
        Row {
            OverviewItem(topContent = "$currStreak", bottomContent = "Current streak")
            OverviewItem(
                topContent = "${viewModel.bestStreak.intValue}",
                bottomContent = "Best streak"
            )
            OverviewItem(
                topContent = "$totalPomodori",
                bottomContent = "Total Pomodori"
            )

        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                val count = history[day.date]?.completedSessions ?: 0
                Day(day, count, today, oldestDate, currentDaySelection)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatDate(currentDaySelection.value.toString()),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
//                textAlign = TextAlign.Start
            )
//            
            Text(
                text = "${
                    history[currentDaySelection.value]?.completedSessions ?: 0
                } pomodori",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
//                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun OverviewItem(topContent: String, bottomContent: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = topContent, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = bottomContent, style = MaterialTheme.typography.labelSmall)
    }
}


@Composable
fun Day(
    day: CalendarDay,
    count: Int,
    today: LocalDate,
    oldestDate: LocalDate?,
    currentDaySelection: MutableState<LocalDate>
) {
    val isInstalled = oldestDate != null && day.date >= oldestDate

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                when {
                    count > 0 && day.date == today -> FocusColors.primary
                    count > 0 -> FocusColors.primary.copy(alpha = 0.4f)
                    day.date > today -> MaterialTheme.colorScheme.surfaceVariant
                    day.date == today -> FocusColors.secondary.copy(alpha = 0.3f)
                    !isInstalled -> MaterialTheme.colorScheme.surfaceVariant
                    else -> BreakColors.primary.copy(alpha = 0.25f)
                }
            )
            .clickable {
                currentDaySelection.value = day.date
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            count > 0 -> SelectableImage(
                painter = painterResource(R.drawable.tomato),
                contentDescription = null,
                modifier = Modifier,
                selected = currentDaySelection.value == day.date
            )

            isInstalled && day.date < today -> SelectableImage(
                painter = painterResource(R.drawable.potato_small),
                contentDescription = null,
                modifier = Modifier,
                selected = currentDaySelection.value == day.date
            )

            else -> SelectableImage(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier,
                selected = currentDaySelection.value == day.date
            )
        }
    }
}

fun formatDate(dateString: String): String {
    // Define the format of the input string
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // Parse the input string into a LocalDate object
    val date = LocalDate.parse(dateString, inputFormatter)
    // Define the desired output format
    val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    // Format the date and return the result
    return date.format(outputFormatter)
}

@Composable
fun SelectableImage(
    selected: Boolean,
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val imageSize by animateDpAsState(
        targetValue = if (selected) 80.dp else 50.dp,
        label = "imageSize"
    )

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = Modifier.requiredSize(imageSize)
    )
}