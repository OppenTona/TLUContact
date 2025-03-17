package com.example.tlucontact;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class dbcbnvViewHolder extends RecyclerView.ViewHolder{

    private TextView name, position, department, phoneNumber, email;
    private ImageView avatar;
    public dbcbnvViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.textView_FullName);
        position = itemView.findViewById(R.id.textView_Position);
        department = itemView.findViewById(R.id.textView_Department);
        phoneNumber = itemView.findViewById(R.id.textView_PhoneNumber);
        email = itemView.findViewById(R.id.textView_Email);
        avatar = itemView.findViewById(R.id.imageView_DB);

    }

    public void bind(dbcbnv dbcbnv){
        name.setText(dbcbnv.getName());
        position.setText(dbcbnv.getPosition());
        department.setText(dbcbnv.getDepartment());
        phoneNumber.setText(dbcbnv.getPhoneNumber());
        email.setText(dbcbnv.getEmail());
        avatar.setImageResource(dbcbnv.getAvatar());
    }
}
