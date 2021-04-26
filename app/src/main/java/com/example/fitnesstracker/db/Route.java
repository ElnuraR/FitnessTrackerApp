package com.example.fitnesstracker.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "routes_table")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String length;

    private String speed;

    private String time;

    public Route(String length, String speed, String time) {
        this.length = length;
        this.speed = speed;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
