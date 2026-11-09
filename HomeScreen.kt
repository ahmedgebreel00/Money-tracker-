package com.example.moneytracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.data.CategoryEntity
import com.example.moneytracker.data.TransactionEntity
import com.example.moneytracker.data.TxType
import com.example.moneytracker.ui.theme.LocalMoneyColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: MoneyViewModel,
    currency: String,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val month by vm.month.collectAsStateWithLifecycle()
    val txs by vm.monthTransactions.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()

    val categoryById = remember(categories) { categories.associateBy { it.id } }
    val income = remember(txs) { txs.filter { it.type == TxType.INCOME }.sumOf { it.amount } }
    val expense = remember(txs) { txs.filter { it.type == TxType.EXPENSE }.sumOf { it.amount } }
    val grouped = remember(txs) { txs.groupBy { it.date }.entries.toList() }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Money Tracker", fontWeight = FontWeight.Bold) },
                colors = appTopBarColors()
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Add") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                MonthSelector(
                    month = month,
                    onPrevious = { vm.shiftMonth(-1) },
                    onNext = { vm.shiftMonth(1) }
                )
            }
            item { SummaryCard(income = income, expense = expense, currency = currency) }

            if (txs.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.ReceiptLong,
                        title = "No transactions this month",
                        subtitle = "Tap Add to record your first income or expense."
                    )
                }
            }

            grouped.forEach { (day, list) ->
                item(key = "header-$day") { DayHeader(day = day, list = list, currency = currency) }
                items(list, key = { it.id }) { tx ->
                    TransactionRow(
                        tx = tx,
                        category = categoryById[tx.categoryId],
                        currency = currency,
                        onClick = { onEdit(tx.id) },
                        onDelete = {
                            vm.deleteTransaction(tx)
                            scope.launch {
                                snackbar.currentSnackbarData?.dismiss()
                                val result = snackbar.showSnackbar(
                                    message = "Transaction deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    vm.restoreTransaction(tx)
                                }
                            }
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(income: Double, expense: Double, currency: String) {
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.surface.luminance() < 0.5f
    val brush = if (dark) {
        Brush.linearGradient(listOf(scheme.primaryContainer, scheme.secondaryContainer))
    } else {
        Brush.linearGradient(listOf(scheme.primary, scheme.secondary))
    }
    val foreground = if (dark) scheme.onPrimaryContainer else scheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(brush)
            .padding(24.dp)
    ) {
        Column {
            Text(
                "Balance",
                style = MaterialTheme.typography.titleSmall,
                color = foreground.copy(alpha = 0.8f)
            )
            Text(
                formatMoney(income - expense, currency),
                style = MaterialTheme.typography.headlineMedium,
                color = foreground
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryPill("Income", income, currency, Icons.Rounded.ArrowDownward, foreground, Modifier.weight(1f))
                SummaryPill("Expenses", expense, currency, Icons.Rounded.ArrowUpward, foreground, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    amount: Double,
    currency: String,
    icon: ImageVector,
    foreground: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(foreground.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = foreground, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = foreground.copy(alpha = 0.8f))
            Text(
                formatMoney(amount, currency),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = foreground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DayHeader(day: Long, list: List<TransactionEntity>, currency: String) {
    val net = list.sumOf { if (it.type == TxType.INCOME) it.amount else -it.amount }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            dayLabel(day),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            (if (net >= 0) "+" else "-") + formatMoney(kotlin.math.abs(net), currency),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionRow(
    tx: TransactionEntity,
    category: CategoryEntity?,
    currency: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val money = LocalMoneyColors.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        category?.name ?: "Uncategorized",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (tx.note.isNotBlank()) {
                        Text(
                            tx.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                val isIncome = tx.type == TxType.INCOME
                Text(
                    (if (isIncome) "+" else "-") + formatMoney(tx.amount, currency),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) money.income else money.expense
                )
            }
        }
    }
}
