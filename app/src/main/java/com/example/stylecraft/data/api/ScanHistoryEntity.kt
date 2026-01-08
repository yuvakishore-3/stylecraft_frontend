package com.example.stylecraft.data.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val faceShape: String,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)
