package com.example.signdetect.domain.processing

import android.graphics.Bitmap
import android.graphics.Color
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import javax.inject.Inject

class SignatureProcessor @Inject constructor() {
    
    fun preprocessImage(bitmap: Bitmap): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        
        // Convert to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
        
        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, Size(5.0, 5.0), 0.0)
        
        // Apply Otsu's thresholding
        val thresholdMat = Mat()
        Imgproc.threshold(grayMat, thresholdMat, 0.0, 255.0, 
            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        
        // Convert back to bitmap
        val processedBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(thresholdMat, processedBitmap)
        
        // Clean up
        mat.release()
        grayMat.release()
        thresholdMat.release()
        
        return processedBitmap
    }
    
    fun extractFeatures(bitmap: Bitmap): List<SignatureFeature> {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        
        // Convert to grayscale if not already
        val grayMat = Mat()
        if (mat.channels() > 1) {
            Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
        } else {
            mat.copyTo(grayMat)
        }
        
        // Find contours
        val contours = mutableListOf<Mat>()
        val hierarchy = Mat()
        Imgproc.findContours(
            grayMat,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        
        // Extract features from contours
        val features = contours.mapNotNull { contour ->
            val area = Imgproc.contourArea(contour)
            val perimeter = Imgproc.arcLength(contour, true)
            
            if (area > MIN_CONTOUR_AREA) {
                SignatureFeature(
                    area = area,
                    perimeter = perimeter,
                    circularity = 4 * Math.PI * area / (perimeter * perimeter)
                )
            } else null
        }
        
        // Clean up
        mat.release()
        grayMat.release()
        hierarchy.release()
        contours.forEach { it.release() }
        
        return features
    }
    
    companion object {
        private const val MIN_CONTOUR_AREA = 100.0
    }
}

data class SignatureFeature(
    val area: Double,
    val perimeter: Double,
    val circularity: Double
) 