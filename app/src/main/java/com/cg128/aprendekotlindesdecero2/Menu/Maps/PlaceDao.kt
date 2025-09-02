package com.cg128.aprendekotlindesdecero2.Menu.Maps

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao

interface PlaceDao {
    @Insert
    suspend fun insert(place: Place)

    @Update
    suspend fun update(place: Place)

    @Query("SELECT * FROM Place")
    suspend fun getAll(): List<Place>

    @Query("SELECT * FROM Place WHERE id = :id")
    suspend fun getById(id: Int): Place?


    @Delete
    suspend fun delete(place: Place)

    @Query("DELETE FROM Place WHERE id = :placeId")
    suspend fun deletePlaceById(placeId: Int)

}