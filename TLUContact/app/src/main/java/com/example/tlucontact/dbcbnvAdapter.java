package com.example.tlucontact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class dbcbnvAdapter extends RecyclerView.Adapter<dbcbnvViewHolder>{
    private dbcbnv[] dbcbnvs;

    public dbcbnvAdapter(dbcbnv[] dbcbnvs){
        this.dbcbnvs = dbcbnvs;
    }

    @NonNull
    @Override
    public dbcbnvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dbcbnv, parent, false);
        return new dbcbnvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull dbcbnvViewHolder holder, int position) {
        holder.bind(dbcbnvs[position]);

    }

    @Override
    public int getItemCount() {
        return dbcbnvs.length;
    }
}
