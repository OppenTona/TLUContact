package com.example.tlucontact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class dbdvDetailBottomSheetFragment extends BottomSheetDialogFragment {
    private TextView name, department, phone, email;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dbdv_dialog, container, false);
        name = view.findViewById(R.id.textView);

        // Nhận dữ liệu từ arguments
        if (getArguments() != null) {
            name.setText(getArguments().getString("FULL_NAME"));
            department.setText(getArguments().getString("DEPARTMENT"));
            phone.setText(getArguments().getString("PHONE"));
            email.setText(getArguments().getString("EMAIL"));
        }

        return view;
    }
}

