package com.ebixcash.offlinefirstapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ebixcash.offlinefirstapp.data.model.Movie

@Database(
    entities = [Movie::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
