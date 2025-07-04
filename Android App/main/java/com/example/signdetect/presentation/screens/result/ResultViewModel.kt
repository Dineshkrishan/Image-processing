package com.example.signdetect.presentation.screens.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signdetect.domain.model.SignatureAnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val resultId: String = checkNotNull(savedStateHandle["resultId"])
    
    private val _resultState = MutableStateFlow<ResultState>(ResultState.Loading)
    val resultState: StateFlow<ResultState> = _resultState
    
    init {
        loadResult()
    }
    
    private fun loadResult() {
        viewModelScope.launch {
            try {
                // TODO: Load result from repository using resultId
                // For now, we'll use dummy data
                val dummyResult = SignatureAnalysisResult(
                    similarityScore = 0.87f,
                    isForged = false,
                    confidenceLevel = 0.92f,
                    matchedFeatures = 42,
                    totalFeatures = 50
                )
                _resultState.value = ResultState.Success(dummyResult)
            } catch (e: Exception) {
                _resultState.value = ResultState.Error(e.message ?: "Failed to load result")
            }
        }
    }
}

sealed class ResultState {
    object Loading : ResultState()
    data class Success(val result: SignatureAnalysisResult) : ResultState()
    data class Error(val message: String) : ResultState()
} 