package com.example.signdetect.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.signdetect.data.dao.SignatureComparisonDao
import com.example.signdetect.data.model.SignatureComparisonRecord

@Database(
    entities = [SignatureComparisonRecord::class],
    version = 1,
    exportSchema = false
)
abstract class SignatureDatabase : RoomDatabase() {
    abstract fun signatureComparisonDao(): SignatureComparisonDao
} 