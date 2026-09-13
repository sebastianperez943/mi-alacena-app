package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY CASE WHEN expirationDate IS NULL THEN 1 ELSE 0 END, expirationDate ASC, name ASC")
    fun getAllItems(): Flow<List<PantryItem>>

    @Query("SELECT * FROM pantry_items WHERE name LIKE '%' || :query || '%' ORDER BY CASE WHEN expirationDate IS NULL THEN 1 ELSE 0 END, expirationDate ASC, name ASC")
    fun searchItems(query: String): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItem): Long

    @Update
    suspend fun updateItem(item: PantryItem)

    @Delete
    suspend fun deleteItem(item: PantryItem)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
