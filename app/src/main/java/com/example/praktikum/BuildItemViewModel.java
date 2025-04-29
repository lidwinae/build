package com.example.praktikum;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class BuildItemViewModel extends ViewModel {
    private BuildItemRepository repository;
    private LiveData<List<BuildItem>> availableItems;
    private LiveData<List<BuildItem>> collectionItems;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void init(BuildItemRepository repository) {
        this.repository = repository;
        availableItems = repository.getAllAvailableItems();
        collectionItems = repository.getAllCollectionItems();
    }

    public LiveData<List<BuildItem>> getAvailableItems() {
        return availableItems;
    }

    public LiveData<List<BuildItem>> getCollectionItems() {
        return collectionItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void insert(BuildItem buildItem) {
        repository.insert(buildItem);
    }

    public void update(BuildItem buildItem) {
        repository.update(buildItem);
    }

    public void delete(BuildItem buildItem) {
        repository.delete(buildItem);
    }

    public void checkAndInsertDefaultItems() {
        new Thread(() -> {
            List<BuildItem> defaultItems = repository.getDefaultItemsSync();
            if (defaultItems == null || defaultItems.isEmpty()) {
                // Insert default items
                BuildItem pantheon = new BuildItem("Pantheon", R.drawable.pantheon, true, true);
                BuildItem joglo = new BuildItem("Joglo", R.drawable.joglo, true, true);
                BuildItem modern = new BuildItem("Modern", R.drawable.modern, true, true);

                repository.insert(pantheon);
                repository.insert(joglo);
                repository.insert(modern);
            }
        }).start();
    }
}
