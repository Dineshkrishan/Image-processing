package com.example.signdetect.presentation.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.signdetect.R
import com.example.signdetect.domain.model.SignatureAnalysisResult

@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val resultState by viewModel.resultState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis Result") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = resultState) {
            is ResultState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResultState.Success -> {
                ResultContent(
                    result = state.result,
                    modifier = Modifier.padding(paddingValues),
                    onNavigateHome = onNavigateHome
                )
            }
            is ResultState.Error -> {
                ErrorContent(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues),
                    onRetry = { /* TODO: Implement retry */ }
                )
            }
        }
    }
}

@Composable
private fun ResultContent(
    result: SignatureAnalysisResult,
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Result Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (result.isAuthentic) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (result.isAuthentic) R.drawable.ic_check
                    else R.drawable.ic_warning
                ),
                contentDescription = if (result.isAuthentic) "Authentic" else "Forged",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Result Title
        Text(
            text = if (result.isAuthentic) "Signature is Authentic"
            else "Potential Forgery Detected",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Confidence Level
        Text(
            text = "Confidence Level: ${(result.confidenceLevel * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Detailed Results
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ResultRow(
                    label = "Similarity Score",
                    value = "${(result.similarityScore * 100).toInt()}%"
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                ResultRow(
                    label = "Matched Features",
                    value = "${result.matchedFeatures}/${result.totalFeatures}"
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                ResultRow(
                    label = "Match Percentage",
                    value = "${result.matchPercentage.toInt()}%"
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action Buttons
        Button(
            onClick = onNavigateHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Back to Home")
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Retry")
        }
    }
} 