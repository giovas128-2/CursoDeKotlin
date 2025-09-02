package com.cg128.aprendekotlindesdecero2.Menu.Maps


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Place")
data class Place(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUri: String // URI de la imagen guardada
)
