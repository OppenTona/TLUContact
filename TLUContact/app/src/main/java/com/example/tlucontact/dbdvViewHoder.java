package com.example.tlucontact;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class dbdvViewHolder extends RecyclerView.ViewHolder{

    private TextView fullName, department, phoneNumber, email;
    private ImageView avatar;

    public dbdvViewHolder(@NonNull View itemView) {
        super(itemView);
        fullName = itemView.findViewById(R.id.textView_FullName);
        department = itemView.findViewById(R.id.textView_Department);
        phoneNumber = itemView.findViewById(R.id.textView_PhoneNumber);
        email = itemView.findViewById(R.id.textView_Email);
        avatar = itemView.findViewById(R.id.imageView_DB);
    }

    public void bind(dbdv dbdv){
        fullName.setText(dbdv.getFullName());
        department.setText(dbdv.getDepartment());
        phoneNumber.setText(dbdv.getPhoneNumber());
        email.setText(dbdv.getEmail());
        avatar.setImageResource(dbdv.getAvatar());
    }
}
