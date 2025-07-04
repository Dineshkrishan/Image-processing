package com.example.signdetect.utils

import android.content.Context
import android.util.Log
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenCVManager @Inject constructor(private val context: Context) {
    
    private val loaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")
                    isOpenCVInitialized = true
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }
    
    var isOpenCVInitialized = false
        private set
    
    fun initializeOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, loaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }
    
    companion object {
        private const val TAG = "OpenCVManager"
    }
} 