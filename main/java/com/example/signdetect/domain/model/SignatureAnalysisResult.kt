package com.example.signdetect.domain.model

data class SignatureAnalysisResult(
    val similarityScore: Float,
    val isForged: Boolean,
    val confidenceLevel: Float,
    val matchedFeatures: Int,
    val totalFeatures: Int,
    val analysisTimestamp: Long = System.currentTimeMillis()
) {
    val matchPercentage: Float
        get() = (matchedFeatures.toFloat() / totalFeatures.toFloat()) * 100

    val isAuthentic: Boolean
        get() = !isForged

    companion object {
        const val FORGERY_THRESHOLD = 0.85f
        const val HIGH_CONFIDENCE_THRESHOLD = 0.95f
    }
} 