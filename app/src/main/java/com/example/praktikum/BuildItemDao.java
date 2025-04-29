package com.example.praktikum;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BuildItemDao {
    @Insert
    void insert(BuildItem buildItem);

    @Update
    void update(BuildItem buildItem);

    @Delete
    void delete(BuildItem buildItem);

    @Query("SELECT * FROM build_items WHERE is_available = 1")
    LiveData<List<BuildItem>> getAllAvailableItems();

    @Query("SELECT * FROM build_items WHERE is_available = 0")
    LiveData<List<BuildItem>> getAllCollectionItems();

    @Query("SELECT * FROM build_items WHERE is_default = 1")
    List<BuildItem> getDefaultItemsSync();

    @Query("SELECT * FROM build_items WHERE is_default = 0")
    LiveData<List<BuildItem>> getUserCreatedItems();
}
