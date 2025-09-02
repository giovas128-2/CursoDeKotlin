package com.cg128.aprendekotlindesdecero2.Menu.Maps

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Place::class], version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}
