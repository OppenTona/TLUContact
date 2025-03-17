package com.example.tlucontact;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class dbdvActivity extends AppCompatActivity implements dbdvAdapter.OnItemClickListener {

    private RecyclerView recyclerView_dbdv;
    private dbdv[] dbdvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dbdv);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dữ liệu danh sách nhân viên/đơn vị
        dbdvs = new dbdv[]{
                new dbdv("01", "Nguyễn Quỳnh Diệp", "P206", "0904.345.673", "diepnq@tlu.edu.vn", R.drawable.codiep),
                new dbdv("02", "Trương Xuân Nam", "P206", "0912.102.165", "namtx@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("03", "Nguyễn Thanh Tùng", "P206", "0913.008.694", "tungnt@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("04", "Vũ Anh Dũng", "P206", "0984.113.303", "dungva@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("05", "Lê Đức Hậu", "P206", "0912.324.564", "duchaule@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("06", "Bùi Thị Thanh Xuân", "P206", "0902.001.581", "xuanbtt@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("07", "Bùi Thị Thu Cúc", "P206", "0382.627.574", "cucbt@tlu.edu.vn", R.drawable.macdinh),
                new dbdv("08", "Phạm Văn Tùng", "P206", "0987.950.986", "phamvantung@tlu.edu.vn", R.drawable.macdinh)
        };

        recyclerView_dbdv = findViewById(R.id.RecyclerView_dbdv);
        recyclerView_dbdv.setLayoutManager(new LinearLayoutManager(this));

        // Gán Adapter và truyền sự kiện click
        dbdvAdapter adapter = new dbdvAdapter(dbdvs, this);
        recyclerView_dbdv.setAdapter(adapter);
    }

    @Override
    public void onItemClick(dbdv selectedItem) {
        // Tạo BottomSheetDialogFragment
        dbdvDetailBottomSheetFragment bottomSheet = new dbdvDetailBottomSheetFragment();

        // Truyền dữ liệu vào BottomSheetDialogFragment
        Bundle args = new Bundle();
        args.putString("FULL_NAME", selectedItem.getFullName());
        args.putString("DEPARTMENT", selectedItem.getDepartment());
        args.putString("PHONE", selectedItem.getPhoneNumber());
        args.putString("EMAIL", selectedItem.getEmail());
        args.putInt("IMAGE", selectedItem.getAvatar());

        bottomSheet.setArguments(args);
        bottomSheet.show(getSupportFragmentManager(), "dbdvDetailBottomSheetFragment");
    }
}
