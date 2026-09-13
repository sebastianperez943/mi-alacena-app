package com.example.data

import kotlinx.coroutines.flow.Flow

class PantryRepository(
    private val pantryDao: PantryDao,
    private val shoppingDao: ShoppingDao
) {
    val allPantryItems: Flow<List<PantryItem>> = pantryDao.getAllItems()

    fun searchPantryItems(query: String): Flow<List<PantryItem>> {
        return if (query.isBlank()) {
            pantryDao.getAllItems()
        } else {
            pantryDao.searchItems(query.trim())
        }
    }

    suspend fun insertPantryItem(item: PantryItem): Long = pantryDao.insertItem(item)

    suspend fun updatePantryItem(item: PantryItem) = pantryDao.updateItem(item)

    suspend fun deletePantryItem(item: PantryItem) = pantryDao.deleteItem(item)

    suspend fun deletePantryItemById(id: Long) = pantryDao.deleteById(id)

    val allShoppingItems: Flow<List<ShoppingItem>> = shoppingDao.getAllItems()

    suspend fun insertShoppingItem(item: ShoppingItem): Long = shoppingDao.insertItem(item)

    suspend fun updateShoppingItem(item: ShoppingItem) = shoppingDao.updateItem(item)

    suspend fun deleteShoppingItem(item: ShoppingItem) = shoppingDao.deleteItem(item)

    suspend fun deleteShoppingItemById(id: Long) = shoppingDao.deleteById(id)

    suspend fun clearCheckedShoppingItems() = shoppingDao.clearChecked()

    suspend fun transferShoppingToPantry(shoppingItem: ShoppingItem) {
        val pantryItem = PantryItem(
            name = shoppingItem.name,
            quantity = if (shoppingItem.quantity.isBlank()) "1" else shoppingItem.quantity
        )
        pantryDao.insertItem(pantryItem)
        shoppingDao.deleteItem(shoppingItem)
    }

    suspend fun addPantryItemToShoppingList(pantryItem: PantryItem) {
        val shoppingItem = ShoppingItem(
            name = pantryItem.name,
            quantity = pantryItem.quantity.ifBlank { "1" }
        )
        shoppingDao.insertItem(shoppingItem)
    }
}
