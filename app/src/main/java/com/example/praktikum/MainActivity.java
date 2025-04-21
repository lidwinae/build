package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.example.praktikum.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BuildAdapter.OnBuildClickListener {
    private ActivityMainBinding binding;
    private BuildAdapter availableAdapter;
    private BuildAdapter collectionAdapter;
    private List<BuildItem> availableItems = new ArrayList<>();
    private List<BuildItem> collectionItems = new ArrayList<>();
    private boolean useGambar1 = true;

    // Key untuk menyimpan state
    private static final String KEY_AVAILABLE_ITEMS = "available_items";
    private static final String KEY_COLLECTION_ITEMS = "collection_items";
    private static final String KEY_USE_GAMBAR1 = "use_gambar1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cek jika ada saved state
        if (savedInstanceState != null) {
            // Restore data dari saved state
            availableItems = savedInstanceState.getParcelableArrayList(KEY_AVAILABLE_ITEMS);
            collectionItems = savedInstanceState.getParcelableArrayList(KEY_COLLECTION_ITEMS);
            useGambar1 = savedInstanceState.getBoolean(KEY_USE_GAMBAR1);
        } else {
            // Inisialisasi data default jika tidak ada saved state
            initializeAvailableItems();
        }

        setupRecyclerViews();
        setupAddButton();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Simpan state saat rotasi layar
        outState.putParcelableArrayList(KEY_AVAILABLE_ITEMS, new ArrayList<>(availableItems));
        outState.putParcelableArrayList(KEY_COLLECTION_ITEMS, new ArrayList<>(collectionItems));
        outState.putBoolean(KEY_USE_GAMBAR1, useGambar1);
    }

    private void setupRecyclerViews() {
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
                collectionItems.add(newItem);
                collectionAdapter.notifyItemInserted(collectionItems.size() - 1);
                binding.etInput.setText("");
            }
        });
    }

    private void initializeAvailableItems() {
        availableItems.add(new BuildItem("Pantheon", R.drawable.pantheon, true));
        availableItems.add(new BuildItem("Joglo", R.drawable.joglo, true));
        availableItems.add(new BuildItem("Modern", R.drawable.modern, true));
    }

    @Override
    public void onAddClick(BuildItem item) {
        availableItems.remove(item);
        collectionItems.add(item);
        availableAdapter.notifyDataSetChanged();
        collectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteClick(BuildItem item) {
        collectionItems.remove(item);
        collectionAdapter.notifyDataSetChanged();

        if (item.isAvailable()) {
            availableItems.add(item);
            availableAdapter.notifyDataSetChanged();
        }
    }
}