package com.example.fitnesstracker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RouteDao {

    @Insert
    void insert(Route route);

    @Query("SELECT * FROM routes_table")
    LiveData<List<Route>> getAllRoutes();
}
