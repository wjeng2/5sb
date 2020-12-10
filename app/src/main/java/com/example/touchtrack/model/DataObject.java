package com.example.touchtrack.model;

import java.util.ArrayList;
import java.util.List;

public class DataObject {
    private String userName;
    private List<SwipeData> sdl;
    private int width;
    private int height;

    public DataObject(String userName, List<SwipeData> sdl, int width, int height) {
        this.userName= userName;
        this.sdl = sdl;
        this.width = width;
        this.height = height;
    }
}