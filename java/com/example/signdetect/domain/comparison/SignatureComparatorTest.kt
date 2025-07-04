package com.example.signdetect.domain.comparison

import android.graphics.Bitmap
import com.example.signdetect.domain.model.SignatureAnalysisResult
import com.example.signdetect.domain.processing.SignatureFeature
import com.example.signdetect.domain.processing.SignatureProcessor
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SignatureComparatorTest {
    
    private lateinit var signatureProcessor: SignatureProcessor
    private lateinit var signatureComparator: SignatureComparator
    private lateinit var mockBitmap1: Bitmap
    private lateinit var mockBitmap2: Bitmap
    
    @Before
    fun setup() {
        signatureProcessor = mockk()
        signatureComparator = SignatureComparator(signatureProcessor)
        mockBitmap1 = mockk()
        mockBitmap2 = mockk()
    }
    
    @Test
    fun `when signatures are similar, should return high similarity score`() {
        // Given
        val features1 = listOf(
            SignatureFeature(area = 100.0, perimeter = 40.0, circularity = 0.8),
            SignatureFeature(area = 150.0, perimeter = 50.0, circularity = 0.85)
        )
        val features2 = listOf(
            SignatureFeature(area = 102.0, perimeter = 41.0, circularity = 0.81),
            SignatureFeature(area = 148.0, perimeter = 49.0, circularity = 0.84)
        )
        
        every { signatureProcessor.preprocessImage(mockBitmap1) } returns mockBitmap1
        every { signatureProcessor.preprocessImage(mockBitmap2) } returns mockBitmap2
        every { signatureProcessor.extractFeatures(mockBitmap1) } returns features1
        every { signatureProcessor.extractFeatures(mockBitmap2) } returns features2
        
        // When
        val result = signatureComparator.compareSignatures(mockBitmap1, mockBitmap2)
        
        // Then
        assertFalse(result.isForged)
        assertTrue(result.similarityScore >= SignatureAnalysisResult.FORGERY_THRESHOLD)
    }
    
    @Test
    fun `when signatures are different, should return low similarity score`() {
        // Given
        val features1 = listOf(
            SignatureFeature(area = 100.0, perimeter = 40.0, circularity = 0.8),
            SignatureFeature(area = 150.0, perimeter = 50.0, circularity = 0.85)
        )
        val features2 = listOf(
            SignatureFeature(area = 200.0, perimeter = 80.0, circularity = 0.6),
            SignatureFeature(area = 250.0, perimeter = 90.0, circularity = 0.65)
        )
        
        every { signatureProcessor.preprocessImage(mockBitmap1) } returns mockBitmap1
        every { signatureProcessor.preprocessImage(mockBitmap2) } returns mockBitmap2
        every { signatureProcessor.extractFeatures(mockBitmap1) } returns features1
        every { signatureProcessor.extractFeatures(mockBitmap2) } returns features2
        
        // When
        val result = signatureComparator.compareSignatures(mockBitmap1, mockBitmap2)
        
        // Then
        assertTrue(result.isForged)
        assertTrue(result.similarityScore < SignatureAnalysisResult.FORGERY_THRESHOLD)
    }
    
    @Test
    fun `should calculate correct number of matched features`() {
        // Given
        val features1 = listOf(
            SignatureFeature(area = 100.0, perimeter = 40.0, circularity = 0.8),
            SignatureFeature(area = 150.0, perimeter = 50.0, circularity = 0.85)
        )
        val features2 = listOf(
            SignatureFeature(area = 101.0, perimeter = 40.5, circularity = 0.81),
            SignatureFeature(area = 200.0, perimeter = 70.0, circularity = 0.7)
        )
        
        every { signatureProcessor.preprocessImage(mockBitmap1) } returns mockBitmap1
        every { signatureProcessor.preprocessImage(mockBitmap2) } returns mockBitmap2
        every { signatureProcessor.extractFeatures(mockBitmap1) } returns features1
        every { signatureProcessor.extractFeatures(mockBitmap2) } returns features2
        
        // When
        val result = signatureComparator.compareSignatures(mockBitmap1, mockBitmap2)
        
        // Then
        assertEquals(1, result.matchedFeatures)
        assertEquals(2, result.totalFeatures)
    }
} 