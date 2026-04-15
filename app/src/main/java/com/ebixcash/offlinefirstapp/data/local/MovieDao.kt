package com.ebixcash.offlinefirstapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ebixcash.offlinefirstapp.data.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY lastModified DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE syncedWithFirebase = 0 ORDER BY lastModified ASC")
    fun getUnsyncedMovies(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: String)

    @Query("UPDATE movies SET syncedWithFirebase = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE movies SET syncedWithFirebase = 1")
    suspend fun markAllAsSynced()
}



