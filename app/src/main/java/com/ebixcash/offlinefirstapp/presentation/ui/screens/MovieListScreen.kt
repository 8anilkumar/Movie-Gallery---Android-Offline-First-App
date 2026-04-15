package com.ebixcash.offlinefirstapp.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ebixcash.offlinefirstapp.presentation.ui.components.MovieCardNew
import com.ebixcash.offlinefirstapp.presentation.viewmodel.MovieListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    onAddMovieClick: () -> Unit,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val internetMessage = viewModel.internetConnectionMessage.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    
    val lifecycleOwner = LocalLifecycleOwner.current
    val isAppInForeground = remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> isAppInForeground.value = true
                Lifecycle.Event.ON_PAUSE -> isAppInForeground.value = false
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(error.value) {
        error.value?.let { errorMessage ->
            snackBarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(internetMessage.value) {
        if (isAppInForeground.value) {
            internetMessage.value?.let { message ->
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearInternetMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Gallery") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMovieClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Movie",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Show movies if available, even if loading
                movies.value.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(movies.value) { movie ->
                            MovieCardNew(
                                movie = movie,
                                onDelete = { viewModel.deleteMovie(movie.id) },
                                onLike = { viewModel.likeMovie(movie) },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }
                }
                // Show loading only if no movies yet
                isLoading.value -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Show empty state
                else -> {
                    Text(
                        text = "No movies yet. Click + to add one!",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
