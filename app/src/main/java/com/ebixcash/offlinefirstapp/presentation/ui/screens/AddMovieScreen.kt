package com.ebixcash.offlinefirstapp.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ebixcash.offlinefirstapp.presentation.viewmodel.AddMovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    onBackClick: () -> Unit,
    viewModel: AddMovieViewModel = hiltViewModel()
) {
    val movieName = viewModel.movieName.collectAsState()
    val movieThumbnail = viewModel.movieThumbnail.collectAsState()
    val releaseDate = viewModel.releaseDate.collectAsState()
    val rating = viewModel.rating.collectAsState()
    val description = viewModel.description.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val success = viewModel.success.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error.value) {
        error.value?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(success.value) {
        if (success.value) {
            snackbarHostState.showSnackbar(
                message = "Movie added successfully!",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccess()
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Movie") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Movie Name
                OutlinedTextField(
                    value = movieName.value,
                    onValueChange = { viewModel.setMovieName(it) },
                    label = { Text("Movie Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading.value,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Thumbnail URL
                OutlinedTextField(
                    value = movieThumbnail.value,
                    onValueChange = { viewModel.setMovieThumbnail(it) },
                    label = { Text("Thumbnail URL (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading.value,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Release Date
                OutlinedTextField(
                    value = releaseDate.value,
                    onValueChange = { viewModel.setReleaseDate(it) },
                    label = { Text("Release Date *") },
                    placeholder = { Text("e.g., 2024-04-10") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading.value
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rating
                OutlinedTextField(
                    value = rating.value,
                    onValueChange = { viewModel.setRating(it) },
                    label = { Text("Rating (0-10) *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description.value,
                    onValueChange = { viewModel.setDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    enabled = !isLoading.value,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = { viewModel.addMovie() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading.value
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    } else {
                        Text("Add Movie")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
