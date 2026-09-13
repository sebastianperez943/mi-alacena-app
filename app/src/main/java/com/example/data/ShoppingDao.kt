package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, createdAt DESC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem): Long

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun clearChecked()
}
