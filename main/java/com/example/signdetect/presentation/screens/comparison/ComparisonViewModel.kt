package com.example.signdetect.presentation.screens.comparison

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signdetect.domain.comparison.SignatureComparator
import com.example.signdetect.domain.model.SignatureAnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signatureComparator: SignatureComparator
) : ViewModel() {
    
    private val _comparisonState = MutableStateFlow<ComparisonState>(ComparisonState.Idle)
    val comparisonState: StateFlow<ComparisonState> = _comparisonState
    
    fun compareSignatures(originalUri: Uri, testUri: Uri, resultId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _comparisonState.value = ComparisonState.Loading
                
                val originalBitmap = uriToBitmap(originalUri)
                val testBitmap = uriToBitmap(testUri)
                
                val result = signatureComparator.compareSignatures(originalBitmap, testBitmap)
                
                _comparisonState.value = ComparisonState.Success(result)
            } catch (e: Exception) {
                _comparisonState.value = ComparisonState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    private fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }
}

sealed class ComparisonState {
    object Idle : ComparisonState()
    object Loading : ComparisonState()
    data class Success(val result: SignatureAnalysisResult) : ComparisonState()
    data class Error(val message: String) : ComparisonState()
} 