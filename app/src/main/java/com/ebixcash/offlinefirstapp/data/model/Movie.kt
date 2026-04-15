package com.ebixcash.offlinefirstapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var thumbnail: String = "",
    var releaseDate: String = "",
    var rating: Float = 0f,
    var description: String = "",
    var likes: Int = 0,
    var syncedWithFirebase: Boolean = false,
    var lastModified: Long = System.currentTimeMillis()
)
