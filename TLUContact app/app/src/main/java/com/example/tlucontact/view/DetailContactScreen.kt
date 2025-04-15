package com.example.tlucontact.view

import android.Manifest // Thư viện dùng để yêu cầu quyền truy cập hệ thống, ví dụ quyền camera, storage
import android.app.Activity // Thư viện để tương tác với Activity trong Android
import android.content.Context // Thư viện cung cấp ngữ cảnh của ứng dụng (Context)
import android.content.Intent // Thư viện để tạo và quản lý Intent (chuyển màn hình)
import android.content.pm.PackageManager // Thư viện để làm việc với các gói ứng dụng (PackageManager)
import android.net.Uri // Thư viện hỗ trợ làm việc với URI (địa chỉ tài nguyên như file, ảnh, v.v.)
import androidx.compose.foundation.Image // Thư viện Jetpack Compose cho phép hiển thị hình ảnh
import androidx.compose.foundation.background // Thư viện Compose cho phép thêm màu nền vào các thành phần UI
import androidx.compose.foundation.clickable // Thư viện Compose để làm cho phần tử có thể nhấn được (clickable)
import androidx.compose.foundation.layout.* // Thư viện Compose hỗ trợ các layout như Column, Row, Box, Spacer, v.v.
import androidx.compose.foundation.rememberScrollState // Thư viện Compose hỗ trợ việc ghi nhớ trạng thái cuộn của danh sách
import androidx.compose.foundation.shape.CircleShape // Thư viện Compose cung cấp các hình dạng như CircleShape, RoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape // Thư viện Compose cung cấp hình dạng với các góc bo tròn
import androidx.compose.foundation.verticalScroll // Thư viện Compose cho phép cuộn dọc trong màn hình
import androidx.compose.material.icons.Icons // Thư viện chứa các icon có sẵn trong Material Design
import androidx.compose.material.icons.filled.* // Nhóm icon sẵn có từ Material Design (ví dụ: icons cho camera, phone)
import androidx.compose.material3.* // Thư viện Compose Material 3 với các component như Button, TextField, Icon
import androidx.compose.runtime.* // Thư viện hỗ trợ các tính năng state trong Compose (State, LaunchedEffect, v.v.)
import androidx.compose.ui.Alignment // Thư viện Compose hỗ trợ việc căn chỉnh các phần tử UI
import androidx.compose.ui.Modifier // Thư viện Compose cho phép tùy chỉnh các đặc tính của phần tử UI như size, padding
import androidx.compose.ui.draw.clip // Thư viện Compose cho phép cắt phần tử UI theo hình dạng tùy chỉnh
import androidx.compose.ui.graphics.Color // Thư viện Compose cung cấp màu sắc
import androidx.compose.ui.platform.LocalContext // Thư viện Compose cung cấp ngữ cảnh của ứng dụng từ môi trường hiện tại
import androidx.compose.ui.text.font.FontWeight // Thư viện Compose cho phép điều chỉnh trọng lượng chữ trong text
import androidx.compose.ui.unit.dp // Đơn vị đo lường cho các kích thước trong Compose, sử dụng dp (density-independent pixels)
import androidx.compose.ui.unit.sp // Đơn vị đo lường cho kích thước văn bản trong Compose, sử dụng sp (scale-independent pixels)
import androidx.core.app.ActivityCompat // Thư viện hỗ trợ kiểm tra và yêu cầu quyền của ứng dụng trong Activity
import androidx.core.content.ContextCompat // Thư viện hỗ trợ kiểm tra quyền truy cập ứng dụng trong ngữ cảnh
import coil.compose.rememberAsyncImagePainter // Thư viện Coil dùng để tải và hiển thị ảnh từ URL trong Compose
import com.example.tlucontact.data.model.Staff // Đối tượng Staff từ dữ liệu của ứng dụng (ví dụ giảng viên)
import androidx.compose.ui.graphics.vector.ImageVector // Thư viện hỗ trợ xử lý vector image trong Compose (icon, v.v.)


@OptIn(ExperimentalMaterial3Api::class) // Cho phép sử dụng API đang ở giai đoạn thử nghiệm
@Composable // Đánh dấu hàm là một Composable, có thể được sử dụng trong giao diện người dùng Compose
fun DetailContactScreen(staff: Staff, onBack: () -> Unit) { // Hàm hiển thị thông tin chi tiết của cán bộ giảng viên
    val scrollState = rememberScrollState() // Ghi nhớ trạng thái cuộn để dùng trong vertical scroll

    Scaffold( // Scaffold giúp tạo layout có top bar, content và snackbar, v.v.
        topBar = { // Thanh top bar
            TopAppBar( // Thanh tiêu đề
                title = { Text("Thông tin cán bộ giảng viên", color = Color.Black) }, // Tiêu đề thanh top bar
                navigationIcon = { // Nút quay lại
                    IconButton(onClick = onBack) { // Nút quay lại
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black) // Icon quay lại
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White) // Màu nền trắng cho top bar
            )
        }
    ) { paddingValues -> // Nội dung bên trong scaffold
        Column( // Cột chứa các phần tử UI
            modifier = Modifier // Modifier dùng để tùy chỉnh các thuộc tính của phần tử UI
                .fillMaxSize() // cung cấp kích thước tối đa cho phần tử UI
                .background(Color.White) // Màu nền trắng
                .verticalScroll(scrollState) // Cho phép cuộn
                .padding(paddingValues) // Padding từ scaffold
                .padding(16.dp), // Lề
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các phần tử theo chiều ngang
        ) {
            Image( // ảnh
                painter = rememberAsyncImagePainter(model = staff.avatarURL), // Hiển thị ảnh đại diện từ URL
                contentDescription = "Avatar", // Mô tả content
                modifier = Modifier // Chỉnh sửa
                    .size(100.dp) // cỡ
                    .clip(CircleShape) // Bo tròn thành hình tròn
            )

            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách

            Text( // Hiển thị tên
                text = staff.name, // Tên gán với tên giảng viên
                fontSize = 20.sp, // cỡ chữ 20
                fontWeight = FontWeight.Bold, // độ đậm của chữ
                color = Color.Black // màu chữ là màu đen
            )

            Spacer(modifier = Modifier.height(16.dp)) // tạo khoảng trống giữa các phần tử

            Row( // Dòng chứa các nút chức năng
                modifier = Modifier.fillMaxWidth(), // đặt cỡ max size cả chiều dài và rộng
                horizontalArrangement = Arrangement.SpaceEvenly // Giãn đều
            ) {
                // Thể hiện là 1 nút được gắn với icon, icon mẫu được lấy trong Material Icons của Jetpack Compose
                ActionButton(icon = Icons.Filled.Chat, label = "Tin nhắn", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.Phone, label = "Gọi", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.VideoCall, label = "Gọi video", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.Email, label = "Mail", phoneNumber = staff.email)
            }
                // tạo khoảng trống với chiều cao 24dp
            Spacer(modifier = Modifier.height(24.dp))

            // Các trường thông tin tĩnh
            // Trường mã giảng viên có nhãn là Mã giảng viên trong đó giá trị được gán bằng staffid
            InfoField(label = "Mã giảng viên", value = staff.staffId)
            InfoField(label = "Chức vụ", value = staff.position)
            InfoField(label = "Số điện thoại", value = staff.phone)
            InfoField(label = "Email", value = staff.email)
            InfoField(label = "Đơn vị", value = staff.department)
            InfoField(label = "Ghi chú", value = "-")
        }
    }
}


@Composable
// Nút chức năng với icon và nhãn
fun ActionButton(icon: ImageVector, label: String, phoneNumber: String? = null) { // Hàm tạo nút chức năng
    val context = LocalContext.current // Lấy context để gọi intent

    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Cột chứa icon và nhãn
        IconButton(onClick = { // Xử lý khi nhấn nút
            // Xử lý hành động tương ứng với nút được nhấn
            when (label) { // Kiểm tra nhãn của nút
                "Gọi" -> phoneNumber?.let { makePhoneCall(context, it) } // Gọi điện thoại
                "Tin nhắn" -> phoneNumber?.let { sendSMS(context, it) } // Gửi tin nhắn
                "Mail" -> phoneNumber?.let { sendEmail(context, it) } // Gửi email
                "Gọi video" -> phoneNumber?.let { openVideoCallApp(context, it) } // Gọi video
            }
        }) {
            // Icon hiển thị trên nút
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF007AFF))
        }

        Text( // Label dưới nút
            text = label, // Nhãn của nút
            fontSize = 12.sp, // cỡ chữ 12
            color = Color(0xFF007AFF), // Màu chữ
            modifier = Modifier.clickable { // Xử lý khi nhấn vào chữ
                // Cũng xử lý khi người dùng nhấn vào chữ
                when (label) { // Kiểm tra nhãn
                    "Gọi" -> phoneNumber?.let { makePhoneCall(context, it) } // Gọi điện thoại
                    "Tin nhắn" -> phoneNumber?.let { sendSMS(context, it) } // Gửi tin nhắn
                    "Mail" -> phoneNumber?.let { sendEmail(context, it) } // Gửi email
                    "Gọi video" -> phoneNumber?.let { openVideoCallApp(context, it) } // Gọi video
                }
            }
        )
    }
}


fun sendSMS(context: Context, phoneNumber: String) { // Gửi tin nhắn SMS
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber")) // Mở app SMS với số đã điền
    context.startActivity(intent) // Bắt đầu activity với intent
}

fun sendEmail(context: Context, email: String) { // Gửi email
    val intent = Intent(Intent.ACTION_SENDTO).apply { // Tạo intent gửi email
        data = Uri.parse("mailto:$email") // Mở app email với email đã điền
    }
    context.startActivity(intent) // Bắt đầu activity với intent
}

fun openVideoCallApp(context: Context, phoneNumber: String) { // Mở ứng dụng gọi video
    val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.meetings") // Gọi Google Meet
    if (intent != null) { // Nếu ứng dụng đã cài
        context.startActivity(intent) // Bắt đầu activity với intent
    } else { // Nếu chưa cài
        // Nếu chưa cài, mở Google Play để cài
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.meetings")) //  Mở Google Play
        context.startActivity(playStoreIntent) // Bắt đầu activity với intent
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable  // Trường thông tin với nhãn và giá trị
fun InfoField(label: String, value: String) { // Hàm tạo trường thông tin
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { // Cột chứa nhãn và giá trị
        OutlinedTextField( // Trường thông tin
            value = value, // Giá trị của trường thông tin
            onValueChange = { }, // Không cho chỉnh sửa
            modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
            readOnly = true, // Chỉ hiển thị, không chỉnh sửa
            shape = RoundedCornerShape(16.dp), // Bo tròn các góc
            label = { Text(label, color = Color.Gray) }, // Nhãn ở viền ngoài
            colors = TextFieldDefaults.colors( // Màu sắc các trạng thái
                focusedTextColor = Color.Black, // Màu chữ khi có tiêu điểm
                unfocusedTextColor = Color.Black, // Màu chữ khi không có tiêu điểm
                disabledTextColor = Color.Black, // Màu chữ khi bị vô hiệu hóa
                focusedContainerColor = Color.White, // Màu nền khi có tiêu điểm
                unfocusedContainerColor = Color.White, // Màu nền khi không có tiêu điểm
                disabledContainerColor = Color(0xFFE0E0E0), // Màu nền khi bị vô hiệu hóa
                focusedIndicatorColor = Color(0xFF007AFF), // Màu viền khi có tiêu điểm
                unfocusedIndicatorColor = Color(0xFFE0E0E0), // Màu viền khi không có tiêu điểm
                disabledIndicatorColor = Color.Transparent // Màu viền khi bị vô hiệu hóa
            )
        )
    }
}
// Hàm này sẽ được gọi khi người dùng nhấn vào nút gọi điện thoại
fun makePhoneCall(context: Context, phoneNumber: String) { // Gọi điện thoại
    // Kiểm tra xem ứng dụng có quyền gọi điện hay không
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        // Nếu chưa được cấp quyền gọi điện, yêu cầu quyền
        ActivityCompat.requestPermissions( // Yêu cầu quyền gọi điện
            (context as Activity), // Chuyển đổi context thành Activity
            arrayOf(Manifest.permission.CALL_PHONE), // Yêu cầu quyền gọi điện
            1 // Mã yêu cầu quyền
        )
    } else { // Nếu đã có quyền gọi điện
        val intent = Intent(Intent.ACTION_CALL) // Gọi trực tiếp
        intent.data = Uri.parse("tel:$phoneNumber") // Đặt số điện thoại vào intent
        context.startActivity(intent) // Bắt đầu activity với intent
    }
}
