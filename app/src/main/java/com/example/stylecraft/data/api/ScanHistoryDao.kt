package com.example.stylecraft.data.api

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestScan(): Flow<ScanHistoryEntity?>

    @Query("SELECT * FROM scan_history WHERE id = :scanId")
    suspend fun getScanById(scanId: Int): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long

    @Delete
    suspend fun deleteScan(scan: ScanHistoryEntity)

    @Query("DELETE FROM scan_history")
    suspend fun deleteAllScans()

    @Query("SELECT COUNT(*) FROM scan_history")
    fun getScanCount(): Flow<Int>

    @Query("SELECT faceShape, COUNT(*) as count FROM scan_history GROUP BY faceShape")
    fun getFaceShapeStats(): Flow<List<FaceShapeStats>>
}

data class FaceShapeStats(
    val faceShape: String,
    val count: Int
)