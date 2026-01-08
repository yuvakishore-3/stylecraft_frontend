package com.example.stylecraft.data.api

import com.example.stylecraft.data.api.AppDatabase
import com.example.stylecraft.data.api.FaceShapeStats
import com.example.stylecraft.data.api.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanHistoryRepository(private val database: AppDatabase) {

    private val dao = database.scanHistoryDao()

    fun getAllScans(): Flow<List<ScanHistoryEntity>> = dao.getAllScans()

    fun getLatestScan(): Flow<ScanHistoryEntity?> = dao.getLatestScan()

    suspend fun getScanById(scanId: Int): ScanHistoryEntity? = dao.getScanById(scanId)

    suspend fun saveScan(faceShape: String, confidence: Float, imageUri: String? = null): Long {
        return dao.insertScan(
            ScanHistoryEntity(
                faceShape = faceShape,
                confidence = confidence,
                imageUri = imageUri
            )
        )
    }

    suspend fun deleteScan(scan: ScanHistoryEntity) = dao.deleteScan(scan)

    suspend fun deleteAllScans() = dao.deleteAllScans()

    fun getScanCount(): Flow<Int> = dao.getScanCount()

    fun getFaceShapeStats(): Flow<List<FaceShapeStats>> = dao.getFaceShapeStats()
}
