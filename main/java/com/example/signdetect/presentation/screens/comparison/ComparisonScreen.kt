package com.example.signdetect.presentation.screens.comparison

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.signdetect.R
import java.io.File
import java.util.UUID

@Composable
fun ComparisonScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    var originalSignatureUri by remember { mutableStateOf<Uri?>(null) }
    var testSignatureUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Handle the captured image
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            if (originalSignatureUri == null) {
                originalSignatureUri = it
            } else {
                testSignatureUri = it
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Signatures") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Original Signature Section
            Text(
                text = "Original Signature",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (originalSignatureUri != null) {
                    AsyncImage(
                        model = originalSignatureUri,
                        contentDescription = "Original Signature",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload),
                            contentDescription = "Upload",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to upload or capture",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Test Signature Section
            Text(
                text = "Test Signature",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (testSignatureUri != null) {
                    AsyncImage(
                        model = testSignatureUri,
                        contentDescription = "Test Signature",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload),
                            contentDescription = "Upload",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to upload or capture",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (originalSignatureUri != null && testSignatureUri != null) {
                        val resultId = UUID.randomUUID().toString()
                        viewModel.compareSignatures(
                            originalUri = originalSignatureUri!!,
                            testUri = testSignatureUri!!,
                            resultId = resultId
                        )
                        onNavigateToResult(resultId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = originalSignatureUri != null && testSignatureUri != null
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_compare),
                    contentDescription = "Compare"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Compare Signatures")
            }
        }
    }
} 