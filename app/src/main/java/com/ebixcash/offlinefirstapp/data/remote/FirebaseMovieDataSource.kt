package com.ebixcash.offlinefirstapp.data.remote

import android.util.Log
import com.ebixcash.offlinefirstapp.data.model.Movie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseSync"

@Singleton
class FirebaseMovieDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val databaseRef = firebaseDatabase.reference
    private val moviesRef = databaseRef.child("movies")

    init {
        Log.d(TAG, "FirebaseMovieDataSource initialized")
        checkFirebaseConnection()
    }

    fun getAllMovies(): Flow<List<Movie>> = callbackFlow {
        Log.d(TAG, "getAllMovies() - Creating flow")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val movies = mutableListOf<Movie>()
                    snapshot.children.forEach { childSnapshot ->
                        try {
                            val key = childSnapshot.key
                            val movie = childSnapshot.getValue(Movie::class.java)
                            if (movie != null) {
                                movies.add(movie)
                            } else {
                                Log.w(TAG, "   ✗ Failed to parse: $key (null)")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "   ✗ Error parsing: ${childSnapshot.key} - ${e.message}")
                        }
                    }
                    
                    Log.d(TAG, "   📤 Sending ${movies.size} movies via flow")
                    trySend(movies)
                    Log.d(TAG, "   ✅ Flow emission successful")
                } catch (e: Exception) {
                    Log.e(TAG, "   ✗ Exception: ${e.message}", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "❌ onCancelled - ${error.code}: ${error.message}")
                close(error.toException())
            }
        }

        Log.d(TAG, "   📡 Adding listener to ${moviesRef.path}")
        moviesRef.addValueEventListener(listener)
        
        awaitClose {
            Log.d(TAG, "   🛑 Removing listener")
            moviesRef.removeEventListener(listener)
        }
    }

    suspend fun addMovie(movie: Movie) {
        try {
            moviesRef.child(movie.id).setValue(movie).await()
        } catch (e: Exception) {
            throw Exception("Failed to add movie to Firebase: ${e.message}")
        }
    }

    suspend fun updateMovie(movie: Movie) {
        try {
            moviesRef.child(movie.id).setValue(movie).await()
        } catch (e: Exception) {
            throw Exception("Failed to update movie in Firebase: ${e.message}")
        }
    }

    suspend fun deleteMovie(movieId: String) {
        try {
            moviesRef.child(movieId).removeValue().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete movie from Firebase: ${e.message}")
        }
    }

    private fun checkFirebaseConnection() {
        databaseRef.child(".info/connected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                Log.d(TAG, "Firebase connected: $connected")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Connection check error: ${error.message}")
            }
        })
    }
}
