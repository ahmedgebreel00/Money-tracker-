package com.example.moneytracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.AppDatabase
import com.example.moneytracker.data.CategoryEntity
import com.example.moneytracker.data.DarkMode
import com.example.moneytracker.data.DefaultCategories
import com.example.moneytracker.data.Settings
import com.example.moneytracker.data.SettingsRepository
import com.example.moneytracker.data.TransactionEntity
import com.example.moneytracker.data.TxType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class MoneyViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val categoryDao = db.categoryDao()
    private val txDao = db.transactionDao()
    private val settingsRepo = SettingsRepository(app)

    val categories: StateFlow<List<CategoryEntity>> = categoryDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = txDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Null until the saved settings have been read, so the UI never flashes the wrong theme. */
    val settings: StateFlow<Settings?> = settingsRepo.settings
        .map<Settings, Settings?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _month = MutableStateFlow(YearMonth.now())
    val month: StateFlow<YearMonth> = _month.asStateFlow()

    val monthTransactions: StateFlow<List<TransactionEntity>> =
        combine(transactions, _month) { list, month ->
            list.filter { YearMonth.from(LocalDate.ofEpochDay(it.date)) == month }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        // Seed the starter categories once, so deleting them all later doesn't bring them back.
        viewModelScope.launch {
            if (!settingsRepo.isSeeded()) {
                if (categoryDao.count() == 0) categoryDao.insertAll(DefaultCategories.all)
                settingsRepo.markSeeded()
            }
        }
    }

    // ---- Month navigation ----
    fun shiftMonth(delta: Int) {
        _month.value = _month.value.plusMonths(delta.toLong())
    }

    // ---- Transactions ----
    fun saveTransaction(
        id: Long,
        amount: Double,
        type: TxType,
        categoryId: Long?,
        note: String,
        date: Long
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = id,
                amount = amount,
                type = type,
                categoryId = categoryId,
                note = note,
                date = date
            )
            if (id == 0L) txDao.insert(tx) else txDao.update(tx)
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch { txDao.delete(tx) }
    }

    /** Used by "Undo": re-inserts the transaction with its original id. */
    fun restoreTransaction(tx: TransactionEntity) {
        viewModelScope.launch { txDao.insert(tx) }
    }

    // ---- Categories ----
    fun saveCategory(category: CategoryEntity) {
        viewModelScope.launch {
            if (category.id == 0L) categoryDao.insert(category) else categoryDao.update(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { categoryDao.delete(category) }
    }

    // ---- Settings ----
    fun setTheme(id: String) {
        viewModelScope.launch {
            settingsRepo.setTheme(id)
            settingsRepo.setDynamicColor(false)
        }
    }

    fun setDarkMode(mode: DarkMode) {
        viewModelScope.launch { settingsRepo.setDarkMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setDynamicColor(enabled) }
    }

    fun setCurrency(code: String) {
        viewModelScope.launch { settingsRepo.setCurrency(code) }
    }
}
