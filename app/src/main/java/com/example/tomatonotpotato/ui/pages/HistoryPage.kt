package com.example.tomatonotpotato.ui.pages


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.example.tomatonotpotato.R
import com.example.tomatonotpotato.data.AppOpenRepository
import com.example.tomatonotpotato.data.PomodoroRecord
import com.example.tomatonotpotato.ui.theme.BreakColors
import com.example.tomatonotpotato.ui.theme.FocusColors
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState


import java.time.LocalDate
import java.time.YearMonth

private val daySize = 18.dp

@Composable
fun HistoryPage(viewModel: PomodoroViewModel = viewModel()) {
//    val today = remember { LocalDate.now() }
    val today = PomodoroViewModel.TODAY
    // Ensure range covers 2 weeks before and after today
    val startMonth = remember { YearMonth.from(today.minusWeeks(2)) }
    val endMonth = remember { YearMonth.from(today.plusWeeks(2)) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(today),
    )

    val oldestDate by viewModel.oldestDate.collectAsState()

    PomodoroCalendar(
        state = state, viewModel = viewModel, today = today, oldestDate = oldestDate
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
    oldestDate: LocalDate?
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
                Day(day, count, today, oldestDate)
            }
        )
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
fun Day(day: CalendarDay, count: Int, today: LocalDate, oldestDate: LocalDate?) {
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
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            count > 0 -> Image(
                painterResource(R.drawable.tomato),
                null,
                Modifier.requiredSize(50.dp)
            )

            isInstalled && day.date < today -> Image(
                painterResource(R.drawable.potato_small),
                null,
                Modifier.requiredSize(50.dp)
            )

            else -> Image(
                painterResource(R.drawable.ic_launcher_foreground),
                null,
                Modifier.requiredSize(50.dp)
            )
        }
    }
}

// if (day.date == today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant