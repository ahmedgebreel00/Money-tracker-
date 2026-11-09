package com.example.moneytracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.data.TxType
import com.example.moneytracker.ui.theme.LocalMoneyColors
import java.time.YearMonth

private data class Slice(val name: String, val color: Color, val value: Double)

private data class MonthTotals(val month: YearMonth, val income: Double, val expense: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(vm: MoneyViewModel, currency: String) {
    val month by vm.month.collectAsStateWithLifecycle()
    val monthTxs by vm.monthTransactions.collectAsStateWithLifecycle()
    val allTxs by vm.transactions.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()

    var type by rememberSaveable { mutableStateOf(TxType.EXPENSE) }

    val slices = remember(monthTxs, categories, type) {
        val byId = categories.associateBy { it.id }
        monthTxs
            .filter { it.type == type }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                val category = byId[categoryId]
                Slice(
                    name = category?.name ?: "Uncategorized",
                    color = category?.let { Color(it.colorArgb) } ?: UncategorizedColor,
                    value = list.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.value }
    }
    val total = slices.sumOf { it.value }

    val trend = remember(allTxs, month) {
        (5 downTo 0).map { back ->
            val m = month.minusMonths(back.toLong())
            val inMonth = allTxs.filter { monthOf(it.date) == m }
            MonthTotals(
                month = m,
                income = inMonth.filter { it.type == TxType.INCOME }.sumOf { it.amount },
                expense = inMonth.filter { it.type == TxType.EXPENSE }.sumOf { it.amount }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Reports", fontWeight = FontWeight.Bold) },
                colors = appTopBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            MonthSelector(
                month = month,
                onPrevious = { vm.shiftMonth(-1) },
                onNext = { vm.shiftMonth(1) }
            )
            TypeToggle(
                type = type,
                onChange = { type = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                expenseLabel = "Expenses",
                incomeLabel = "Income"
            )

            if (slices.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.PieChart,
                    title = "Nothing to show",
                    subtitle = "No ${if (type == TxType.EXPENSE) "expenses" else "income"} recorded this month."
                )
            } else {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DonutChart(slices = slices, total = total, currency = currency)
                        Spacer(Modifier.height(16.dp))
                        slices.forEach { slice ->
                            BreakdownRow(slice = slice, total = total, currency = currency)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last 6 months", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    TrendChart(trend)
                }
            }
        }
    }
}

@Composable
private fun DonutChart(slices: List<Slice>, total: Double, currency: String) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    Box(modifier = Modifier.size(220.dp), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            val strokeWidth = 28.dp.toPx()
            val inset = strokeWidth / 2f
            val arcTopLeft = Offset(inset, inset)
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = track,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            if (total > 0.0) {
                val gap = if (slices.size > 1) 2f else 0f
                var start = -90f
                slices.forEach { slice ->
                    val sweep = (slice.value / total * 360.0).toFloat()
                    drawArc(
                        color = slice.color,
                        startAngle = start,
                        sweepAngle = (sweep - gap).coerceAtLeast(0.5f),
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    start += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Total",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                formatMoney(total, currency),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Composable
private fun BreakdownRow(slice: Slice, total: Double, currency: String) {
    val fraction = if (total > 0.0) (slice.value / total).toFloat() else 0f
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(slice.color)
            )
            Text(
                slice.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            Text(
                "${(fraction * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                formatMoney(slice.value, currency),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { fraction },
            color = slice.color,
            trackColor = slice.color.copy(alpha = 0.15f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun TrendChart(data: List<MonthTotals>) {
    val money = LocalMoneyColors.current
    val maxValue = data.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1.0)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val columnWidth = size.width / data.size
        val barWidth = columnWidth * 0.26f
        val gap = 2.dp.toPx()
        val corner = CornerRadius(4.dp.toPx())

        data.forEachIndexed { index, item ->
            val center = columnWidth * index + columnWidth / 2f
            val incomeHeight = (item.income / maxValue * size.height).toFloat()
            val expenseHeight = (item.expense / maxValue * size.height).toFloat()

            if (incomeHeight > 0f) {
                drawRoundRect(
                    color = money.income,
                    topLeft = Offset(center - barWidth - gap, size.height - incomeHeight),
                    size = Size(barWidth, incomeHeight),
                    cornerRadius = corner
                )
            }
            if (expenseHeight > 0f) {
                drawRoundRect(
                    color = money.expense,
                    topLeft = Offset(center + gap, size.height - expenseHeight),
                    size = Size(barWidth, expenseHeight),
                    cornerRadius = corner
                )
            }
        }
    }
    Spacer(Modifier.height(6.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        data.forEach { item ->
            Text(
                shortMonth(item.month),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendDot(money.income, "Income")
        LegendDot(money.expense, "Expenses")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
