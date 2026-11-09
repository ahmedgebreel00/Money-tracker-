package com.example.moneytracker.ui

import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatMoney(amount: Double, currency: String): String {
    val nf = NumberFormat.getNumberInstance().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return "$currency ${nf.format(amount)}"
}

/** Plain number for the amount text field: 12 or 12.50 (always a dot, never grouped). */
fun formatPlain(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toLong().toString()
    else String.format(Locale.US, "%.2f", amount)

fun dayLabel(epochDay: Long): String {
    val date = LocalDate.ofEpochDay(epochDay)
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
    }
}

fun monthLabel(month: YearMonth): String =
    month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

fun shortMonth(month: YearMonth): String =
    month.format(DateTimeFormatter.ofPattern("MMM"))

fun monthOf(epochDay: Long): YearMonth = YearMonth.from(LocalDate.ofEpochDay(epochDay))
