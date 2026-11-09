package com.example.moneytracker.data

object DefaultCategories {
    private fun make(name: String, icon: String, color: Long, type: TxType) =
        CategoryEntity(name = name, iconKey = icon, colorArgb = color.toInt(), type = type)

    val all: List<CategoryEntity> = listOf(
        make("Food & Dining", "restaurant", 0xFFF97316, TxType.EXPENSE),
        make("Groceries", "cart", 0xFF22C55E, TxType.EXPENSE),
        make("Transport", "car", 0xFF3B82F6, TxType.EXPENSE),
        make("Bills & Utilities", "bolt", 0xFFEAB308, TxType.EXPENSE),
        make("Housing", "home", 0xFF8B5CF6, TxType.EXPENSE),
        make("Health", "hospital", 0xFFEF4444, TxType.EXPENSE),
        make("Education", "school", 0xFF06B6D4, TxType.EXPENSE),
        make("Entertainment", "movie", 0xFFEC4899, TxType.EXPENSE),
        make("Shopping", "bag", 0xFFF43F5E, TxType.EXPENSE),
        make("Travel", "flight", 0xFF14B8A6, TxType.EXPENSE),
        make("Other", "category", 0xFF64748B, TxType.EXPENSE),

        make("Salary", "work", 0xFF16A34A, TxType.INCOME),
        make("Freelance", "laptop", 0xFF0EA5E9, TxType.INCOME),
        make("Gifts", "gift", 0xFFDB2777, TxType.INCOME),
        make("Investments", "trending", 0xFF7C3AED, TxType.INCOME),
        make("Other", "savings", 0xFF64748B, TxType.INCOME)
    )
}
