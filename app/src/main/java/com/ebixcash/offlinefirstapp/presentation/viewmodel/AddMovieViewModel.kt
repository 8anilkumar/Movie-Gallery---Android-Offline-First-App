package com.ebixcash.offlinefirstapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebixcash.offlinefirstapp.data.model.Movie
import com.ebixcash.offlinefirstapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _movieName = MutableStateFlow("")
    val movieName: StateFlow<String> = _movieName.asStateFlow()

    private val _movieThumbnail = MutableStateFlow("")
    val movieThumbnail: StateFlow<String> = _movieThumbnail.asStateFlow()

    private val _releaseDate = MutableStateFlow("")
    val releaseDate: StateFlow<String> = _releaseDate.asStateFlow()

    private val _rating = MutableStateFlow("")
    val rating: StateFlow<String> = _rating.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun setMovieName(name: String) {
        _movieName.value = name
    }

    fun setMovieThumbnail(thumbnail: String) {
        _movieThumbnail.value = thumbnail
    }

    fun setReleaseDate(date: String) {
        _releaseDate.value = date
    }

    fun setRating(rating: String) {
        _rating.value = rating
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun addMovie() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                if (_movieName.value.isBlank()) {
                    _error.value = "Movie name cannot be empty"
                    _isLoading.value = false
                    return@launch
                }

                if (_releaseDate.value.isBlank()) {
                    _error.value = "Release date cannot be empty"
                    _isLoading.value = false
                    return@launch
                }

                if (_rating.value.isBlank()) {
                    _error.value = "Rating cannot be empty"
                    _isLoading.value = false
                    return@launch
                }

                val ratingValue = _rating.value.toFloatOrNull() ?: 0f
                if (ratingValue < 0f || ratingValue > 10f) {
                    _error.value = "Rating must be between 0 and 10"
                    _isLoading.value = false
                    return@launch
                }

                val movieId = UUID.randomUUID().toString()
                val movie = Movie(
                    id = movieId,
                    name = _movieName.value.trim(),
                    thumbnail = _movieThumbnail.value.trim(),
                    releaseDate = _releaseDate.value.trim(),
                    rating = ratingValue,
                    description = _description.value.trim(),
                    likes = 0,
                    syncedWithFirebase = false,
                    lastModified = System.currentTimeMillis()
                )

                movieRepository.addMovie(movie)
                
                _success.value = true
                clearFields()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add movie"
                _isLoading.value = false
            }
        }
    }

    private fun clearFields() {
        _movieName.value = ""
        _movieThumbnail.value = ""
        _releaseDate.value = ""
        _rating.value = ""
        _description.value = ""
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccess() {
        _success.value = false
    }
}
