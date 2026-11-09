package com.example.moneytracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.data.TxType
import com.example.moneytracker.ui.theme.LocalMoneyColors
import java.time.LocalDate

private const val MILLIS_PER_DAY = 86_400_000L
private val AmountPattern = Regex("^\\d{0,9}(\\.\\d{0,2})?$")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditScreen(
    vm: MoneyViewModel,
    id: Long,
    currency: String,
    onClose: () -> Unit
) {
    val transactions by vm.transactions.collectAsStateWithLifecycle()
    val categories by vm.categories.collectAsStateWithLifecycle()
    val money = LocalMoneyColors.current

    val existing = remember(id, transactions) {
        if (id == 0L) null else transactions.firstOrNull { it.id == id }
    }

    var type by remember(existing?.id) { mutableStateOf(existing?.type ?: TxType.EXPENSE) }
    var amountText by remember(existing?.id) {
        mutableStateOf(existing?.amount?.let { formatPlain(it) } ?: "")
    }
    var categoryId by remember(existing?.id) { mutableStateOf(existing?.categoryId) }
    var note by remember(existing?.id) { mutableStateOf(existing?.note ?: "") }
    var dateDay by remember(existing?.id) {
        mutableStateOf(existing?.date ?: LocalDate.now().toEpochDay())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var showNewCategory by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val visibleCategories = remember(categories, type) { categories.filter { it.type == type } }
    val accent = if (type == TxType.INCOME) money.income else money.expense

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(if (id == 0L) "Add transaction" else "Edit transaction") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = { showDelete = true }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                        }
                    }
                },
                colors = appTopBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TypeToggle(
                type = type,
                onChange = { newType ->
                    type = newType
                    val selected = categories.firstOrNull { it.id == categoryId }
                    if (selected?.type != newType) categoryId = null
                }
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { value ->
                    val cleaned = value.replace(',', '.')
                    if (AmountPattern.matches(cleaned)) amountText = cleaned
                },
                label = { Text("Amount") },
                prefix = { Text("$currency ") },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Category", style = MaterialTheme.typography.labelLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                visibleCategories.forEach { category ->
                    FilterChip(
                        selected = categoryId == category.id,
                        onClick = { categoryId = category.id },
                        label = { Text(category.name) },
                        leadingIcon = {
                            Icon(
                                CategoryIcons.get(category.iconKey),
                                contentDescription = null,
                                tint = Color(category.colorArgb),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
                AssistChip(
                    onClick = { showNewCategory = true },
                    label = { Text("New") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }

            Text("Date", style = MaterialTheme.typography.labelLarge)
            OutlinedCard(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(dayLabel(dateDay), style = MaterialTheme.typography.bodyLarge)
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(120) },
                label = { Text("Note (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    vm.saveTransaction(
                        id = existing?.id ?: 0L,
                        amount = amount,
                        type = type,
                        categoryId = categoryId,
                        note = note.trim(),
                        date = dateDay
                    )
                    onClose()
                },
                enabled = amount > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateDay * MILLIS_PER_DAY)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { dateDay = Math.floorDiv(it, MILLIS_PER_DAY) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete transaction?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    existing?.let { vm.deleteTransaction(it) }
                    showDelete = false
                    onClose()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("Cancel") }
            }
        )
    }

    if (showNewCategory) {
        CategoryEditorDialog(
            initial = null,
            type = type,
            onSave = {
                vm.saveCategory(it)
                showNewCategory = false
            },
            onDismiss = { showNewCategory = false }
        )
    }
}
