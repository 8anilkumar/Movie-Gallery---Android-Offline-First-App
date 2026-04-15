package com.ebixcash.offlinefirstapp.di

import android.content.Context
import androidx.room.Room
import com.ebixcash.offlinefirstapp.data.local.MovieDao
import com.ebixcash.offlinefirstapp.data.local.MovieDatabase
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideMovieDatabase(
        @ApplicationContext context: Context
    ): MovieDatabase = Room.databaseBuilder(
        context,
        MovieDatabase::class.java,
        "movie_database"
    ).build()

    @Singleton
    @Provides
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        
        try {
            firebaseDatabase.setPersistenceEnabled(true)
        } catch (e: Exception) {
            // Persistence already enabled
        }
        
        return firebaseDatabase
    }
}
