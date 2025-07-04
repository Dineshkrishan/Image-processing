package com.example.signdetect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.signdetect.data.model.SignatureComparisonRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface SignatureComparisonDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComparison(record: SignatureComparisonRecord)
    
    @Query("SELECT * FROM signature_comparisons ORDER BY timestamp DESC")
    fun getAllComparisons(): Flow<List<SignatureComparisonRecord>>
    
    @Query("SELECT * FROM signature_comparisons WHERE id = :id")
    suspend fun getComparisonById(id: String): SignatureComparisonRecord?
    
    @Query("SELECT * FROM signature_comparisons WHERE isForged = 1 ORDER BY timestamp DESC")
    fun getForgedSignatures(): Flow<List<SignatureComparisonRecord>>
    
    @Query("DELETE FROM signature_comparisons WHERE id = :id")
    suspend fun deleteComparison(id: String)
    
    @Query("SELECT COUNT(*) FROM signature_comparisons WHERE isForged = 1")
    fun getForgedSignatureCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM signature_comparisons WHERE isForged = 0")
    fun getAuthenticSignatureCount(): Flow<Int>
} 