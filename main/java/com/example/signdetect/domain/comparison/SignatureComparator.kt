package com.example.signdetect.domain.comparison

import android.graphics.Bitmap
import com.example.signdetect.domain.model.SignatureAnalysisResult
import com.example.signdetect.domain.processing.SignatureFeature
import com.example.signdetect.domain.processing.SignatureProcessor
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Core
import org.opencv.imgproc.Imgproc
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.maxOf
import kotlin.math.pow
import kotlin.math.Math

class SignatureComparator @Inject constructor(
    private val signatureProcessor: SignatureProcessor
) {
    fun compareSignatures(original: Bitmap, test: Bitmap): SignatureAnalysisResult {
        // Preprocess both images
        val processedOriginal = signatureProcessor.preprocessImage(original)
        val processedTest = signatureProcessor.preprocessImage(test)
        
        // Extract features
        val originalFeatures = signatureProcessor.extractFeatures(processedOriginal)
        val testFeatures = signatureProcessor.extractFeatures(processedTest)
        
        // Calculate structural similarity
        val similarityScore = calculateStructuralSimilarity(processedOriginal, processedTest)
        
        // Compare features
        val (matchedFeatures, totalFeatures) = compareFeatures(originalFeatures, testFeatures)
        
        // Calculate confidence level
        val confidenceLevel = calculateConfidenceLevel(similarityScore, matchedFeatures.toFloat() / totalFeatures)
        
        // Determine if forged
        val isForged = similarityScore < SignatureAnalysisResult.FORGERY_THRESHOLD
        
        return SignatureAnalysisResult(
            similarityScore = similarityScore,
            isForged = isForged,
            confidenceLevel = confidenceLevel,
            matchedFeatures = matchedFeatures,
            totalFeatures = totalFeatures
        )
    }
    
    private fun calculateStructuralSimilarity(original: Bitmap, test: Bitmap): Float {
        val originalMat = Mat()
        val testMat = Mat()
        
        Utils.bitmapToMat(original, originalMat)
        Utils.bitmapToMat(test, testMat)
        
        // Convert to grayscale if needed
        val grayOriginal = Mat()
        val grayTest = Mat()
        
        if (originalMat.channels() > 1) {
            Imgproc.cvtColor(originalMat, grayOriginal, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(testMat, grayTest, Imgproc.COLOR_BGR2GRAY)
        } else {
            originalMat.copyTo(grayOriginal)
            testMat.copyTo(grayTest)
        }
        
        // Calculate SSIM
        val C1 = (0.01 * 255).pow(2)
        val C2 = (0.03 * 255).pow(2)
        
        val mu1 = Mat()
        val mu2 = Mat()
        val mu1mu2 = Mat()
        val mu1Sq = Mat()
        val mu2Sq = Mat()
        val sigma1Sq = Mat()
        val sigma2Sq = Mat()
        val sigma12 = Mat()
        
        Imgproc.GaussianBlur(grayOriginal, mu1, org.opencv.core.Size(11.0, 11.0), 1.5)
        Imgproc.GaussianBlur(grayTest, mu2, org.opencv.core.Size(11.0, 11.0), 1.5)
        
        Core.multiply(mu1, mu1, mu1Sq)
        Core.multiply(mu2, mu2, mu2Sq)
        Core.multiply(mu1, mu2, mu1mu2)
        
        Imgproc.GaussianBlur(Core.multiply(grayOriginal, grayOriginal), sigma1Sq, org.opencv.core.Size(11.0, 11.0), 1.5)
        Core.subtract(sigma1Sq, mu1Sq, sigma1Sq)
        
        Imgproc.GaussianBlur(Core.multiply(grayTest, grayTest), sigma2Sq, org.opencv.core.Size(11.0, 11.0), 1.5)
        Core.subtract(sigma2Sq, mu2Sq, sigma2Sq)
        
        Imgproc.GaussianBlur(Core.multiply(grayOriginal, grayTest), sigma12, org.opencv.core.Size(11.0, 11.0), 1.5)
        Core.subtract(sigma12, mu1mu2, sigma12)
        
        // Formula: SSIM = (2*mu1mu2 + C1)(2*sigma12 + C2) / (mu1^2 + mu2^2 + C1)(sigma1^2 + sigma2^2 + C2)
        val ssim = Core.mean(
            ((2 * mu1mu2 + C1) * (2 * sigma12 + C2)) /
            ((mu1Sq + mu2Sq + C1) * (sigma1Sq + sigma2Sq + C2))
        )
        
        // Clean up
        listOf(originalMat, testMat, grayOriginal, grayTest, mu1, mu2, mu1mu2, mu1Sq, mu2Sq, 
               sigma1Sq, sigma2Sq, sigma12).forEach { it.release() }
        
        return ssim.first().toFloat()
    }
    
    private fun compareFeatures(
        originalFeatures: List<SignatureFeature>,
        testFeatures: List<SignatureFeature>
    ): Pair<Int, Int> {
        var matchedFeatures = 0
        val totalFeatures = maxOf(originalFeatures.size, testFeatures.size)
        
        originalFeatures.forEach { original ->
            testFeatures.forEach { test ->
                if (featuresMatch(original, test)) {
                    matchedFeatures++
                }
            }
        }
        
        return Pair(matchedFeatures, totalFeatures)
    }
    
    private fun featuresMatch(f1: SignatureFeature, f2: SignatureFeature): Boolean {
        val areaMatch = abs(f1.area - f2.area) / maxOf(f1.area, f2.area) < FEATURE_MATCH_THRESHOLD
        val perimeterMatch = abs(f1.perimeter - f2.perimeter) / maxOf(f1.perimeter, f2.perimeter) < FEATURE_MATCH_THRESHOLD
        val circularityMatch = abs(f1.circularity - f2.circularity) / maxOf(f1.circularity, f2.circularity) < FEATURE_MATCH_THRESHOLD
        
        return areaMatch && perimeterMatch && circularityMatch
    }
    
    private fun calculateConfidenceLevel(similarityScore: Float, featureMatchRatio: Float): Float {
        return (similarityScore + featureMatchRatio) / 2
    }
    
    companion object {
        private const val FEATURE_MATCH_THRESHOLD = 0.15
    }
}

private fun Double.pow(n: Int): Double = Math.pow(this, n.toDouble()) 