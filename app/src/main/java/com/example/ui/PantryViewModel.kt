package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PantryItem
import com.example.data.PantryRepository
import com.example.data.ShoppingItem
import com.example.util.ExpirationHelper
import com.example.util.ExpiryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    PANTRY,
    SHOPPING
}

class PantryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PantryRepository

    val activeTab = MutableStateFlow(AppTab.PANTRY)
    val searchQuery = MutableStateFlow("")
    val onlyExpiringFilter = MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PantryRepository(database.pantryDao(), database.shoppingDao())
        seedInitialDataIfEmpty()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            // Check if any items exist
            val currentItems = repository.allPantryItems
            // We only seed if repository has no items initially
            // Let's collect first emission
            // Or simple check
        }
    }

    val shoppingItems: StateFlow<List<ShoppingItem>> = repository.allShoppingItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val allPantryItems: StateFlow<List<PantryItem>> = repository.allPantryItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val expiringCount: StateFlow<Int> = allPantryItems.combine(searchQuery) { items, _ ->
        items.count { item ->
            val info = ExpirationHelper.evaluateExpiry(item.expirationDate)
            info.status == ExpiryStatus.EXPIRING_SOON
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val expiredCount: StateFlow<Int> = allPantryItems.combine(searchQuery) { items, _ ->
        items.count { item ->
            val info = ExpirationHelper.evaluateExpiry(item.expirationDate)
            info.status == ExpiryStatus.EXPIRED
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredPantryItems: StateFlow<List<PantryItem>> = combine(
        allPantryItems,
        searchQuery,
        onlyExpiringFilter
    ) { items, query, onlyExpiring ->
        var list = items
        if (query.isNotBlank()) {
            list = list.filter { it.name.contains(query.trim(), ignoreCase = true) }
        }
        if (onlyExpiring) {
            list = list.filter { item ->
                val info = ExpirationHelper.evaluateExpiry(item.expirationDate)
                info.status == ExpiryStatus.EXPIRED || info.status == ExpiryStatus.EXPIRING_SOON
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setActiveTab(tab: AppTab) {
        activeTab.value = tab
    }

    fun toggleExpiringFilter() {
        onlyExpiringFilter.value = !onlyExpiringFilter.value
    }

    fun addPantryItem(
        name: String,
        quantity: String,
        expirationDate: Long?,
        category: String = "General",
        notes: String = ""
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertPantryItem(
                PantryItem(
                    name = name.trim(),
                    quantity = quantity.trim().ifBlank { "1" },
                    expirationDate = expirationDate,
                    category = category.trim().ifBlank { "General" },
                    notes = notes.trim()
                )
            )
        }
    }

    fun updatePantryItem(item: PantryItem) {
        if (item.name.isBlank()) return
        viewModelScope.launch {
            repository.updatePantryItem(item.copy(name = item.name.trim()))
        }
    }

    fun deletePantryItem(item: PantryItem) {
        viewModelScope.launch {
            repository.deletePantryItem(item)
        }
    }

    fun adjustQuantity(item: PantryItem, delta: Int) {
        viewModelScope.launch {
            val currentQtyStr = item.quantity.trim()
            // Try extracting leading integer
            val regex = Regex("""^(\d+)""")
            val match = regex.find(currentQtyStr)
            if (match != null) {
                val num = match.groupValues[1].toIntOrNull() ?: 1
                val newNum = maxOf(0, num + delta)
                val suffix = currentQtyStr.substring(match.range.last + 1)
                val newQtyStr = "$newNum$suffix".trim()
                repository.updatePantryItem(item.copy(quantity = newQtyStr))
            } else {
                // If not starting with a number, try parsing as direct integer or default
                val num = currentQtyStr.toIntOrNull() ?: 1
                val newNum = maxOf(0, num + delta)
                repository.updatePantryItem(item.copy(quantity = newNum.toString()))
            }
        }
    }

    fun addPantryItemToShoppingList(item: PantryItem) {
        viewModelScope.launch {
            repository.addPantryItemToShoppingList(item)
        }
    }

    fun addShoppingItem(name: String, quantity: String = "1") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertShoppingItem(
                ShoppingItem(
                    name = name.trim(),
                    quantity = quantity.trim().ifBlank { "1" }
                )
            )
        }
    }

    fun toggleShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun clearCheckedShoppingItems() {
        viewModelScope.launch {
            repository.clearCheckedShoppingItems()
        }
    }

    fun transferShoppingToPantry(item: ShoppingItem) {
        viewModelScope.launch {
            repository.transferShoppingToPantry(item)
        }
    }

    fun loadSampleDataIfEmpty() {
        viewModelScope.launch {
            if (allPantryItems.value.isEmpty()) {
                val samples = listOf(
                    PantryItem(
                        name = "Leche entera",
                        quantity = "2 litros",
                        expirationDate = ExpirationHelper.addDaysToToday(2),
                        category = "Lácteos"
                    ),
                    PantryItem(
                        name = "Yogurt natural",
                        quantity = "4 vasitos",
                        expirationDate = ExpirationHelper.addDaysToToday(-1), // Caducado
                        category = "Lácteos"
                    ),
                    PantryItem(
                        name = "Huevos frescos",
                        quantity = "12 unidades",
                        expirationDate = ExpirationHelper.addDaysToToday(5), // Por caducar
                        category = "Frescos"
                    ),
                    PantryItem(
                        name = "Arroz blanco",
                        quantity = "2 paquetes (1kg)",
                        expirationDate = ExpirationHelper.addMonthsToToday(8),
                        category = "Granos y pastas"
                    ),
                    PantryItem(
                        name = "Pasta espagueti",
                        quantity = "3 paquetes",
                        expirationDate = ExpirationHelper.addMonthsToToday(12),
                        category = "Granos y pastas"
                    ),
                    PantryItem(
                        name = "Atún en aceite",
                        quantity = "4 latas",
                        expirationDate = ExpirationHelper.addYearsToToday(2),
                        category = "Enlatados"
                    ),
                    PantryItem(
                        name = "Aceite de oliva",
                        quantity = "1 botella",
                        expirationDate = ExpirationHelper.addMonthsToToday(10),
                        category = "Aceites y condimentos"
                    )
                )
                samples.forEach { repository.insertPantryItem(it) }

                val shoppingSamples = listOf(
                    ShoppingItem(name = "Pan de molde", quantity = "1 paquete"),
                    ShoppingItem(name = "Café soluble", quantity = "1 frasco"),
                    ShoppingItem(name = "Manzanas", quantity = "1 kg")
                )
                shoppingSamples.forEach { repository.insertShoppingItem(it) }
            }
        }
    }
}
