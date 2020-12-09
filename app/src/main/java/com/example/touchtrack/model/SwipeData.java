package com.example.touchtrack.model;

public class SwipeData {
    private long ts = 0;
    private float x = 0;
    private float y = 0;
    private float touch_size = 0;
    private float pressure = 0;
    private float velocity_x = 0;
    private float velocity_y = 0;
    private String direction = "";
    private String name = "";

    public SwipeData(long ts, float x, float y, float touch_size, float pressure, float velocity_x, float velocity_y, String dir, String name) {
        this.ts = ts;
        this.x = x;
        this.y = y;
        this.touch_size = touch_size;
        this.pressure = pressure;
        this.velocity_x = velocity_x;
        this.velocity_y = velocity_y;
        this.direction = dir;
        this.name = name;
    }
}