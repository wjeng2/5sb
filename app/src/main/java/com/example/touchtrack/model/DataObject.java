package com.example.touchtrack.model;

import java.util.ArrayList;
import java.util.List;

public class DataObject {
    private String userName;
    private List<SwipeData> sdl;

    public DataObject(String userName, List<SwipeData> sdl) {
        this.userName= userName;
        this.sdl = sdl;
    }
}