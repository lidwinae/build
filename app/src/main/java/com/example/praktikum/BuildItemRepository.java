package com.example.praktikum;

import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BuildItemRepository {
    private BuildItemDao buildItemDao;
    private LiveData<List<BuildItem>> allAvailableItems;
    private LiveData<List<BuildItem>> allCollectionItems;
    private Executor executor = Executors.newSingleThreadExecutor();

    public BuildItemRepository(AppDatabase database) {
        buildItemDao = database.buildItemDao();
        allAvailableItems = buildItemDao.getAllAvailableItems();
        allCollectionItems = buildItemDao.getAllCollectionItems();
    }

    public LiveData<List<BuildItem>> getAllAvailableItems() {
        return allAvailableItems;
    }

    public LiveData<List<BuildItem>> getAllCollectionItems() {
        return allCollectionItems;
    }

    public void insert(BuildItem buildItem) {
        executor.execute(() -> buildItemDao.insert(buildItem));
    }

    public void update(BuildItem buildItem) {
        executor.execute(() -> buildItemDao.update(buildItem));
    }

    public void delete(BuildItem buildItem) {
        executor.execute(() -> buildItemDao.delete(buildItem));
    }

    public List<BuildItem> getDefaultItemsSync() {
        return buildItemDao.getDefaultItemsSync();
    }
}
