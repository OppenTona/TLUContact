package com.example.tlucontact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class dbdvAdapter extends RecyclerView.Adapter<dbdvViewHolder> {
    private dbdv[] dbdvs;
    private OnItemClickListener onItemClickListener; // Interface xử lý click

    // Định nghĩa Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(dbdv item);
    }

    // Constructor nhận thêm OnItemClickListener
    public dbdvAdapter(dbdv[] dbdvs, OnItemClickListener listener) {
        this.dbdvs = dbdvs;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public dbdvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dbdv, parent, false);
        return new dbdvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull dbdvViewHolder holder, int position) {
        dbdv item = dbdvs[position];
        holder.bind(item);

        // Gán sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dbdvs.length;
    }
}