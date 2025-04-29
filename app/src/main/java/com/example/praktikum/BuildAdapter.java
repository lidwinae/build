package com.example.praktikum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.praktikum.databinding.ItemBuildBinding;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BuildAdapter extends RecyclerView.Adapter<BuildAdapter.BuildViewHolder> {
    private List<BuildItem> buildItems = new ArrayList<>();
    private OnBuildClickListener listener;
    private boolean isAvailableList;

    public interface OnBuildClickListener {
        void onAddClick(BuildItem item);
        void onDeleteClick(BuildItem item);
        void onEditClick(BuildItem item);
    }

    public BuildAdapter(boolean isAvailableList, OnBuildClickListener listener) {
        this.isAvailableList = isAvailableList;
        this.listener = listener;
    }

    public void setBuildItems(List<BuildItem> buildItems) {
        this.buildItems = buildItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BuildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBuildBinding binding = ItemBuildBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BuildViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildViewHolder holder, int position) {
        BuildItem item = buildItems.get(position);

        // Load image - prioritize imagePath if available
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            // Load from file path using Glide
            Glide.with(holder.itemView.getContext())
                    .load(new File(item.getImagePath()))
                    .placeholder(R.drawable.gambar1) // Add a placeholder if needed
                    .into(holder.binding.ivItem);
        } else if (item.getImageRes() != 0) {
            // Load from resource if no path is available
            holder.binding.ivItem.setImageResource(item.getImageRes());
        } else {
            // Fallback if neither is available
            holder.binding.ivItem.setImageResource(R.drawable.gambar2);
        }

        holder.binding.tvItem.setText(item.getName());

        // Configure buttons based on list type
        if (isAvailableList) {
            // Available items configuration
            holder.binding.btnTambah.setVisibility(View.VISIBLE);
            holder.binding.btnHapus.setVisibility(View.GONE);
            holder.binding.btnEdit.setVisibility(View.GONE);

            holder.binding.btnTambah.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddClick(item);
                }
            });
        } else {
            // Collection items configuration
            holder.binding.btnTambah.setVisibility(View.GONE);
            holder.binding.btnHapus.setVisibility(View.VISIBLE);

            // Only show edit button for user-created items
            boolean showEditButton = !item.isDefault();
            holder.binding.btnEdit.setVisibility(showEditButton ? View.VISIBLE : View.GONE);

            if (showEditButton) {
                holder.binding.btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditClick(item);
                    }
                });
            }

            holder.binding.btnHapus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return buildItems.size();
    }

    public static class BuildViewHolder extends RecyclerView.ViewHolder {
        ItemBuildBinding binding;

        public BuildViewHolder(ItemBuildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}