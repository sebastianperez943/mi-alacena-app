package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String = "1",
    val expirationDate: Long? = null, // epoch millis
    val category: String = "General",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
