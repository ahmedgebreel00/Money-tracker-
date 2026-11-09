package com.example.moneytracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.data.CategoryEntity
import com.example.moneytracker.data.TxType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(vm: MoneyViewModel) {
    val categories by vm.categories.collectAsStateWithLifecycle()
    val transactions by vm.transactions.collectAsStateWithLifecycle()

    var type by rememberSaveable { mutableStateOf(TxType.EXPENSE) }
    var creating by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<CategoryEntity?>(null) }
    var deleting by remember { mutableStateOf<CategoryEntity?>(null) }

    val list = remember(categories, type) { categories.filter { it.type == type } }
    val counts = remember(transactions) { transactions.groupingBy { it.categoryId }.eachCount() }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.Bold) },
                colors = appTopBarColors()
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { creating = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("New category") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TypeToggle(
                type = type,
                onChange = { type = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (list.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Rounded.Category,
                            title = "No categories yet",
                            subtitle = "Tap New category to create one."
                        )
                    }
                }
                items(list, key = { it.id }) { category ->
                    Card(
                        onClick = { editing = category },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(category)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(category.name, style = MaterialTheme.typography.titleSmall)
                                val count = counts[category.id] ?: 0
                                Text(
                                    if (count == 1) "1 transaction" else "$count transactions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { deleting = category }) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete category")
                            }
                        }
                    }
                }
            }
        }
    }

    if (creating) {
        CategoryEditorDialog(
            initial = null,
            type = type,
            onSave = {
                vm.saveCategory(it)
                creating = false
            },
            onDismiss = { creating = false }
        )
    }

    editing?.let { category ->
        CategoryEditorDialog(
            initial = category,
            type = category.type,
            onSave = {
                vm.saveCategory(it)
                editing = null
            },
            onDismiss = { editing = null }
        )
    }

    deleting?.let { category ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete \"${category.name}\"?") },
            text = {
                Text("Existing transactions in this category are kept and will show as Uncategorized.")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCategory(category)
                    deleting = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleting = null }) { Text("Cancel") }
            }
        )
    }
}

/** Create or edit a category: name, color and icon. Used from Categories and from the Add screen. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryEditorDialog(
    initial: CategoryEntity?,
    type: TxType,
    onSave: (CategoryEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var iconKey by remember { mutableStateOf(initial?.iconKey ?: CategoryIcons.all.keys.first()) }
    var colorArgb by remember { mutableIntStateOf(initial?.colorArgb ?: CategoryColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New category" else "Edit category") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(30) },
                    label = { Text("Name") },
                    singleLine = true
                )

                Text("Color", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryColors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .clickable { colorArgb = c },
                            contentAlignment = Alignment.Center
                        ) {
                            if (c == colorArgb) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Text("Icon", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryIcons.all.forEach { (key, vector) ->
                        val selected = key == iconKey
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) Color(colorArgb).copy(alpha = 0.22f) else Color.Transparent
                                )
                                .then(
                                    if (selected) Modifier.border(2.dp, Color(colorArgb), CircleShape)
                                    else Modifier
                                )
                                .clickable { iconKey = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                vector,
                                contentDescription = key,
                                tint = if (selected) Color(colorArgb) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        CategoryEntity(
                            id = initial?.id ?: 0L,
                            name = name.trim(),
                            iconKey = iconKey,
                            colorArgb = colorArgb,
                            type = initial?.type ?: type
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
