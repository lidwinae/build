package com.example.praktikum;

public class BuildItem {
    private String name;
    private int imageRes;
    private boolean isAvailable;

    public BuildItem(String name, int imageRes, boolean isAvailable) {
        this.name = name;
        this.imageRes = imageRes;
        this.isAvailable = isAvailable;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}