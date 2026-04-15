package com.ebixcash.offlinefirstapp.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebixcash.offlinefirstapp.data.model.Movie
import com.ebixcash.offlinefirstapp.data.repository.MovieRepository
import com.ebixcash.offlinefirstapp.utils.ConnectivityStatus
import com.ebixcash.offlinefirstapp.utils.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MovieListViewModel"

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val networkConnectivityObserver = NetworkConnectivityObserver(context)

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _connectivityStatus = MutableStateFlow<ConnectivityStatus>(ConnectivityStatus.Available)
    val connectivityStatus: StateFlow<ConnectivityStatus> = _connectivityStatus.asStateFlow()

    private val _internetConnectionMessage = MutableStateFlow<String?>(null)
    val internetConnectionMessage: StateFlow<String?> = _internetConnectionMessage.asStateFlow()

    init {
        Log.d(TAG, "🎬 MovieListViewModel initialized")
        loadMovies()
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            networkConnectivityObserver.observe().collect { status ->
                _connectivityStatus.value = status
                when (status) {
                    is ConnectivityStatus.Unavailable -> {
                        Log.w(TAG, "📴 Internet lost")
                        _internetConnectionMessage.value = "📴 Internet connection lost"
                    }
                    is ConnectivityStatus.Available -> {
                        Log.d(TAG, "📡 Internet resumed")
                        _internetConnectionMessage.value = "📡 Internet connection resumed"
                    }
                }
            }
        }
    }

    private fun loadMovies() {
        Log.d(TAG, "🎬 loadMovies() - Collecting from repository")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d(TAG, "   Loading indicator: ON")
                
                movieRepository.getAllMovies().collect { movies ->
                    Log.d(TAG, "   📥 UI received ${movies.size} movies")
                    movies.forEach { movie ->
                        Log.d(TAG, "      • ${movie.name} (${movie.rating}⭐)")
                    }
                    
                    _movies.value = movies
                    _isLoading.value = false
                    
                    Log.d(TAG, "   ✅ UI State updated - displaying ${movies.size} movies")
                    
                    if (movies.isNotEmpty()) {
                        Log.d(TAG, "   🎉 SUCCESS - Movies should now display!")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "   ❌ Error: ${e.message}", e)
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun deleteMovie(movieId: String) {
        viewModelScope.launch {
            try {
                movieRepository.deleteMovie(movieId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun likeMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                val updatedMovie = movie.copy(likes = movie.likes + 1)
                movieRepository.updateMovie(updatedMovie)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearInternetMessage() {
        _internetConnectionMessage.value = null
    }
}
