package com.example.tlucontact;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class dbcbnvActivity extends AppCompatActivity {

    private RecyclerView recyclerView_dbcbnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dbcbnv);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbcbnv[] dbcbnvs = {
                new dbcbnv("01", "Họ và tên: Nguyễn Quỳnh Diệp", "Chức vụ: Trưởng BM", "Cơ quan: P206", "SĐT: 0904.345.673", "Email: diepnq@tlu.edu.vn", R.drawable.codiep),
                new dbcbnv("02", "Họ và tên: Trương Xuân Nam", "Chức vụ: Phó BM", "Cơ quan: P206", "SĐT: 0912.102.165", "Email: namtx@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("03", "Họ và tên: Nguyễn Thanh Tùng", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0913.008.694", "Email: tungnt@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("04", "Họ và tên: Vũ Anh Dũng", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0984.113.303", "Email: dungva@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("05", "Họ và tên: Lê Đức Hậu", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0912.324.564", "Email: duchaule@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("06", "Họ và tên: Bùi Thị Thanh Xuân", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0902.001.581", "Email: xuanbtt@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("07", "Họ và tên: Bùi Thị Thu Cúc", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0382.627.574", "Email: cucbt@tlu.edu.vn", R.drawable.macdinh),
                new dbcbnv("08", "Họ và tên: Phạm Văn Tùng", "Chức vụ: ", "Cơ quan: P206", "SĐT: 0987.950.986", "Email: phamvantung@tlu.edu.vn", R.drawable.macdinh)
        };


        recyclerView_dbcbnv =(RecyclerView ) findViewById(R.id.RecyclerView_dbcbnv);
        recyclerView_dbcbnv.setAdapter(new dbcbnvAdapter(dbcbnvs));

    }
}