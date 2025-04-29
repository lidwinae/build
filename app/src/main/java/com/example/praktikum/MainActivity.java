// MainActivity.java
package com.example.praktikum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.example.praktikum.databinding.ActivityMainBinding;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BuildAdapter.OnBuildClickListener {
    private ActivityMainBinding binding;
    private BuildAdapter availableAdapter;
    private BuildAdapter collectionAdapter;
    private DatabaseHelper dbHelper;
    private boolean useGambar1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi database helper
        dbHelper = new DatabaseHelper(this);

        // Inisialisasi data default jika database kosong
        if (dbHelper.getAllBuildItems().isEmpty()) {
            initializeDefaultItems();
        }

        setupRecyclerViews();
        setupAddButton();
    }

    private void setupRecyclerViews() {
        // Dapatkan data dari database
        List<BuildItem> availableItems = dbHelper.getBuildItemsByAvailability(true);
        List<BuildItem> collectionItems = dbHelper.getBuildItemsByAvailability(false);

        // Setup RecyclerView Available (horizontal)
        availableAdapter = new BuildAdapter(availableItems, true, this);
        binding.rvAvailable.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvAvailable.setAdapter(availableAdapter);

        // Setup RecyclerView Collection (vertical)
        collectionAdapter = new BuildAdapter(collectionItems, false, this);
        binding.rvCollection.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCollection.setAdapter(collectionAdapter);
    }

    private void setupAddButton() {
        binding.btnAdd.setOnClickListener(v -> {
            String input = binding.etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                int imageRes = useGambar1 ? R.drawable.gambar1 : R.drawable.gambar2;
                useGambar1 = !useGambar1;

                BuildItem newItem = new BuildItem(input, imageRes, false);
                dbHelper.addBuildItem(newItem);

                // Refresh RecyclerView
                refreshData();
                binding.etInput.setText("");
            }
        });
    }

    private void initializeDefaultItems() {
        dbHelper.addBuildItem(new BuildItem("Pantheon", R.drawable.pantheon, true));
        dbHelper.addBuildItem(new BuildItem("Joglo", R.drawable.joglo, true));
        dbHelper.addBuildItem(new BuildItem("Modern", R.drawable.modern, true));
    }

    @Override
    public void onAddClick(BuildItem item) {
        // Update status item menjadi tidak tersedia (pindah ke koleksi)
        dbHelper.updateBuildItemAvailability(item, false);
        refreshData();
    }

    @Override
    public void onDeleteClick(BuildItem item) {
        // Jika item awalnya available, kembalikan statusnya
        if (item.isAvailable()) {
            dbHelper.updateBuildItemAvailability(item, true);
        } else {
            // Jika tidak, hapus item
            dbHelper.deleteBuildItem(item);
        }
        refreshData();
    }

    private void refreshData() {
        List<BuildItem> availableItems = dbHelper.getBuildItemsByAvailability(true);
        List<BuildItem> collectionItems = dbHelper.getBuildItemsByAvailability(false);

        availableAdapter.updateData(availableItems);
        collectionAdapter.updateData(collectionItems);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}