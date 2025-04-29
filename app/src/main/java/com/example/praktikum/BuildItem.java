package com.example.praktikum;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "build_items")
public class BuildItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "image_res")
    private int imageRes;

    @ColumnInfo(name = "is_available")
    private boolean isAvailable;

    @ColumnInfo(name = "is_default")
    private boolean isDefault;

    @ColumnInfo(name = "image_path")
    private String imagePath; // Untuk menyimpan path gambar yang diupload

    public BuildItem(String name, int imageRes, boolean isAvailable, boolean isDefault) {
        this.name = name;
        this.imageRes = imageRes;
        this.isAvailable = isAvailable;
        this.isDefault = isDefault;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}