package com.example.praktikum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.praktikum.databinding.ItemBuildBinding;
import java.util.List;

public class BuildAdapter extends RecyclerView.Adapter<BuildAdapter.BuildViewHolder> {
    private List<BuildItem> buildItems;
    private OnBuildClickListener listener;
    private boolean isAvailableList;

    public interface OnBuildClickListener {
        void onAddClick(BuildItem item);
        void onDeleteClick(BuildItem item);
    }

    public BuildAdapter(List<BuildItem> buildItems, boolean isAvailableList, OnBuildClickListener listener) {
        this.buildItems = buildItems;
        this.isAvailableList = isAvailableList;
        this.listener = listener;
    }

    public void updateData(List<BuildItem> newItems) {
        this.buildItems = newItems;
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
        holder.binding.ivItem.setImageResource(item.getImageRes());
        holder.binding.tvItem.setText(item.getName());

        // Atur visibilitas tombol berdasarkan jenis list
        if (isAvailableList) {
            holder.binding.btnTambah.setVisibility(View.VISIBLE);
            holder.binding.btnHapus.setVisibility(View.GONE);

            holder.binding.btnTambah.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddClick(item);
                }
            });
        } else {
            holder.binding.btnTambah.setVisibility(View.GONE);
            holder.binding.btnHapus.setVisibility(View.VISIBLE);

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