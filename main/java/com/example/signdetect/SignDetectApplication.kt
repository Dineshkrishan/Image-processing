package com.example.signdetect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class SignDetectApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            throw RuntimeException("Failed to initialize OpenCV")
        }
    }
} 