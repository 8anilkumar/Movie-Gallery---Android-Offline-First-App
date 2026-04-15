package com.ebixcash.offlinefirstapp.data.repository

import android.util.Log
import com.ebixcash.offlinefirstapp.data.local.MovieDao
import com.ebixcash.offlinefirstapp.data.model.Movie
import com.ebixcash.offlinefirstapp.data.remote.FirebaseMovieDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MovieRepository"

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val firebaseDataSource: FirebaseMovieDataSource
) {

    fun getAllMovies(): Flow<List<Movie>> = flow {
        Log.d(TAG, "📥 getAllMovies() called")
        
        try {
            Log.d(TAG, "   Getting Firebase flow...")
            firebaseDataSource.getAllMovies().collect { firebaseMovies ->
                Log.d(TAG, "   ✅ Received ${firebaseMovies.size} movies from Firebase")
                
                if (firebaseMovies.isNotEmpty()) {
                    Log.d(TAG, "   💾 Saving to Room...")
                    val moviesToSave = firebaseMovies.map { 
                        it.copy(syncedWithFirebase = true)
                    }
                    movieDao.insertMovies(moviesToSave)
                    Log.d(TAG, "   ✅ Saved ${moviesToSave.size} to Room")
                }
                
                Log.d(TAG, "   📤 Emitting ${firebaseMovies.size} movies to UI")
                emit(firebaseMovies)
                Log.d(TAG, "   ✅ Emission complete")
            }
        } catch (e: Exception) {
            Log.e(TAG, "   ❌ Error: ${e.message}", e)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            try {
                movieDao.insertMovie(movie.copy(syncedWithFirebase = false))
                firebaseDataSource.addMovie(movie.copy(syncedWithFirebase = true))
                movieDao.markAsSynced(movie.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding movie: ${e.message}")
            }
        }
    }

    suspend fun updateMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            try {
                movieDao.updateMovie(movie.copy(syncedWithFirebase = false))
                firebaseDataSource.updateMovie(movie.copy(syncedWithFirebase = true))
                movieDao.markAsSynced(movie.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating movie: ${e.message}")
            }
        }
    }

    suspend fun deleteMovie(movieId: String) {
        withContext(Dispatchers.IO) {
            try {
                movieDao.deleteMovieById(movieId)
                firebaseDataSource.deleteMovie(movieId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting movie: ${e.message}")
            }
        }
    }

    suspend fun syncMovies() {
        withContext(Dispatchers.IO) {
            try {
                movieDao.getUnsyncedMovies().collect { unsyncedMovies ->
                    unsyncedMovies.forEach { movie ->
                        try {
                            firebaseDataSource.updateMovie(movie.copy(syncedWithFirebase = true))
                            movieDao.markAsSynced(movie.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Sync error: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed: ${e.message}")
            }
        }
    }

    fun getUnsyncedMovies(): Flow<List<Movie>> = movieDao.getUnsyncedMovies()
}
