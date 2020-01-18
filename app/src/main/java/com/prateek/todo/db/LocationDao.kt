package com.prateek.todo.db

import androidx.room.*

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAll(): List<Location>

    @Query("SELECT * FROM location WHERE created_at > :time")
    fun findByTime(time: Long): Location

    @Query("SELECT * FROM location WHERE lat = :lat")
    fun findByLat(lat: Double): Location

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg location: Location)

    @Delete
    fun delete(currentCoordinate: Location)


    @Query("DELETE FROM location")
    fun deleteAll()

    @Update
    fun updateCurrentCoordinate(vararg currentCoordinate: Location)


}