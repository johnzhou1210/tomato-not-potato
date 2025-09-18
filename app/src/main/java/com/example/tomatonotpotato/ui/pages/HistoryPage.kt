package com.example.tomatonotpotato.ui.pages


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomatonotpotato.R
import com.example.tomatonotpotato.data.PomodoroRecord
import com.example.tomatonotpotato.data.PomodoroViewModel
import com.example.tomatonotpotato.ui.theme.BreakColorsLight
import com.example.tomatonotpotato.ui.theme.FocusColorsLight
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val daySize = 18.dp

@Composable
fun HistoryPage(pomodoroViewModel: PomodoroViewModel = viewModel()) {
//    val today = remember { LocalDate.now() }
    val today = LocalDate.now()
    val oldestDate by pomodoroViewModel.oldestDate.collectAsState()
    // Ensure range covers 2 weeks before and after today
    val startMonth = remember { YearMonth.from(oldestDate) ?: today }
    val endMonth = remember { YearMonth.from(today) }

    val currentDaySelection = remember { mutableStateOf(today) }

    val daysOfWeek = remember { daysOfWeek() }
    val state = rememberCalendarState(
        startMonth = startMonth as YearMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(today),
        firstDayOfWeek = daysOfWeek.first()
    )



    PomodoroCalendar(
        state = state,
        viewModel = pomodoroViewModel,
        today = today,
        oldestDate = oldestDate,
        currentDaySelection = currentDaySelection,
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
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun PomodoroCalendar(
    viewModel: PomodoroViewModel,
    state: CalendarState,
    today: LocalDate,
    oldestDate: LocalDate?,
    currentDaySelection: MutableState<LocalDate>,
) {
    val records by viewModel.records.collectAsState()
    val totalPomodori by viewModel.totalPomodori.collectAsState()
    val pomodoroTimerSettings = viewModel.pomodoroTimerSettings.collectAsState()

    val goal = pomodoroTimerSettings.value.dailyPomodoriGoal
    val history = remember(records) {
        records.associateBy { it.date }
    }

    val currStreak = getStreak(history, today)
    viewModel.updateBestStreak(currStreak)

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "History",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(64.dp))
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
        Spacer(modifier = Modifier.height(64.dp))
        Column(
            modifier = Modifier
                .height(350.dp)
                .padding(horizontal = 8.dp)
        ) {
            HorizontalCalendar(
                monthHeader = { calendarMonth ->
                    val daysOfWeek = calendarMonth.weekDays.first().map { it.date.dayOfWeek }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${
                                calendarMonth.yearMonth.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE,
                                    Locale.getDefault()
                                )
                            } ${calendarMonth.yearMonth.year}",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurface

                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DaysOfWeekTitle(daysOfWeek)
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                },
                state = state,
                dayContent = { day ->
                    val count = history[day.date]?.completedSessions ?: 0
                    Day(
                        day,
                        count,
                        today,
                        oldestDate,
                        currentDaySelection,
                        goal = goal,
                        isThisMonth = day.position == DayPosition.MonthDate
                    )
                },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatDate(currentDaySelection.value.toString()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
//                textAlign = TextAlign.Start
            )
//            
            Text(
                text = "${
                    history[currentDaySelection.value]?.completedSessions ?: 0
                } Pomodori",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
//                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OverviewItem(topContent: String, bottomContent: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = topContent,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = bottomContent,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun Day(
    day: CalendarDay,
    count: Int,
    today: LocalDate,
    oldestDate: LocalDate?,
    currentDaySelection: MutableState<LocalDate>,
    goal: Int = 1,
    isThisMonth: Boolean
) {
    val isInstalled = oldestDate != null && day.date >= oldestDate

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .graphicsLayer(
                alpha = if (isThisMonth) 1f else 0.2f
            )
            .background(
                when {
                    count >= goal && day.date == today -> FocusColorsLight.primary
                    count >= goal -> FocusColorsLight.primary.copy(alpha = 0.4f)
                    day.date > today -> MaterialTheme.colorScheme.surfaceDim
                    day.date == today -> FocusColorsLight.secondary.copy(alpha = 0.3f)
                    !isInstalled -> MaterialTheme.colorScheme.surfaceDim
                    else -> BreakColorsLight.primary.copy(alpha = 0.5f)
                }
            )
            .border(
                width = 2.dp,
                color = if (currentDaySelection.value == day.date) FocusColorsLight.secondary else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable {
                currentDaySelection.value = day.date
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            count >= goal -> SelectableImage(
                painter = painterResource(R.drawable.tomato_high),
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

            else -> {}

        }
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(if (isThisMonth) Color.Transparent else Color.Black.copy(alpha = 0.3f)),
//            contentAlignment = Alignment.Center
//        ){}
    }
}

fun formatDate(dateString: String): String {
    // Define the format of the input string
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    // Parse the input string into a LocalDate object
    val date = LocalDate.parse(dateString, inputFormatter)
    // Define the desired output format
    val outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
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