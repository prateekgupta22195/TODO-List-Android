package com.prateek.todo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location(
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name = "lng") var lng: Double,
    @PrimaryKey var address : String) {
    @ColumnInfo(name = "created_at") var createdAt: Long = System.currentTimeMillis()
}