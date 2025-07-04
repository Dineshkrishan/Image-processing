package com.example.signdetect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.signdetect.domain.model.SignatureAnalysisResult

@Entity(tableName = "signature_comparisons")
data class SignatureComparisonRecord(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val similarityScore: Float,
    val isForged: Boolean,
    val confidenceLevel: Float,
    val matchedFeatures: Int,
    val totalFeatures: Int,
    val originalImagePath: String,
    val testImagePath: String
) {
    companion object {
        fun fromAnalysisResult(
            id: String,
            result: SignatureAnalysisResult,
            originalPath: String,
            testPath: String
        ): SignatureComparisonRecord {
            return SignatureComparisonRecord(
                id = id,
                timestamp = result.analysisTimestamp,
                similarityScore = result.similarityScore,
                isForged = result.isForged,
                confidenceLevel = result.confidenceLevel,
                matchedFeatures = result.matchedFeatures,
                totalFeatures = result.totalFeatures,
                originalImagePath = originalPath,
                testImagePath = testPath
            )
        }
    }
} 