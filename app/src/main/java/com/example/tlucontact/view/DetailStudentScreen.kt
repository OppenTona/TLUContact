package com.example.tlucontact.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailStudentScreen(student: Student, onBack: () -> Unit) {
    // Hàm này nhận vào một đối tượng Student và một lambda function onBack để xử lý sự kiện quay lại.
    val scrollState = rememberScrollState() // Trạng thái cuộn áp dụng cho Column có thể cuộn dọc

    Scaffold(
        topBar = {
            // Định nghĩa thanh ứng dụng ở phía trên màn hình
            TopAppBar(
                // Tạo thanh ứng dụng với tiêu đề và nút điều hướng
                title = { Text("Thông tin sinh viên", color = Color.Black) }, // Hiển thị tiêu đề "Thông tin sinh viên" với màu chữ đen
                navigationIcon = {
                    IconButton(onClick = onBack) {  // Tạo nút biểu tượng có thể nhấn, khi nhấn sẽ gọi hàm onBack
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)  // Hiển thị biểu tượng mũi tên quay lại với màu đen và mô tả "Quay lại" cho accessibility
                    }
                },
                // Thiết lập màu nền của thanh ứng dụng là màu trắng
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->   // paddingValues là giá trị padding được Scaffold cung cấp để tránh nội dung chồng lấp với các thành phần khác
        Column(
            modifier = Modifier
                .fillMaxSize() // Mở rộng để lấp đầy toàn bộ không gian có sẵn trên màn hình
                .background(Color.White) // Đặt màu nền là trắng
                .verticalScroll(scrollState)  // Cho phép cuộn dọc khi nội dung vượt quá kích thước màn hình
                .padding(paddingValues) // Áp dụng padding từ Scaffold để tránh chồng lấp với topBar
                .padding(16.dp), // Thêm padding 16dp xung quanh nội dung
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các thành phần con theo chiều ngang
        ) {
            Image(
                // Hiển thị ảnh đại diện của sinh viên
                // Sử dụng AsyncImagePainter để tải ảnh bất đồng bộ từ URL
                // Nếu photoURL trống, sử dụng ảnh mặc định từ pravatar.cc dựa trên email
                painter = rememberAsyncImagePainter(student.photoURL.ifEmpty { "https://i.pravatar.cc/150?u=${student.email}" }),
                contentDescription = "Ảnh đại diện", // Mô tả nội dung của ảnh cho accessibility
                modifier = Modifier
                    .size(100.dp) // Đặt kích thước ảnh là 100dp x 100dp
                    .clip(CircleShape), // Cắt ảnh thành hình tròn
                contentScale = ContentScale.Crop // Điều chỉnh tỷ lệ ảnh để lấp đầy không gian, có thể cắt bớt phần thừa
            )

            // Tạo khoảng trống 8dp theo chiều dọc giữa ảnh và tên
            Spacer(modifier = Modifier.height(8.dp))
            // Hiển thị tên đầy đủ của sinh viên với kích thước 20sp, kiểu chữ đậm và màu đen
            Text(text = student.fullNameStudent, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            // Tạo khoảng trống 16dp theo chiều dọc giữa tên và các nút hành động
            Spacer(modifier = Modifier.height(16.dp))

            // Lấy context hiện tại để sử dụng trong các Intent khi mở ứng dụng khác
            val context = LocalContext.current

            Row(
                // Sắp xếp các nút theo chiều ngang
                modifier = Modifier.fillMaxWidth(), // Mở rộng để lấp đầy toàn bộ chiều rộng có sẵn
                horizontalArrangement = Arrangement.SpaceEvenly // Phân bố đều các nút theo chiều ngang
            ) {
                // Tạo nút "Tin nhắn" với biểu tượng chat
                StudentActionButton(icon = Icons.Filled.Chat, label = "Tin nhắn") {
                    // Tạo Intent để mở ứng dụng nhắn tin
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("sms:${student.phone}")  // Cấu hình URI với số điện thoại của sinh viên
                    }
                    context.startActivity(intent) // Khởi chạy ứng dụng nhắn tin
                }

                // Tạo nút "Gọi" với biểu tượng điện thoại
                StudentActionButton(icon = Icons.Filled.Phone, label = "Gọi") {
                    // Tạo Intent để mở ứng dụng gọi điện
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${student.phone}") // Cấu hình URI với số điện thoại của sinh viên
                    }
                    context.startActivity(intent) // Khởi chạy ứng dụng gọi điện
                }

                // Tạo nút "Gọi video" với biểu tượng cuộc gọi video
                StudentActionButton(icon = Icons.Filled.VideoCall, label = "Gọi video") {
                    // Bắt đầu khối try để xử lý các ngoại lệ có thể xảy ra
                    try {
                        // Tạo Intent để mở ứng dụng gọi video
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("tel:${student.phone}") // Cấu hình URI với số điện thoại của sinh viên
                        }
                        context.startActivity(intent) // Khởi chạy ứng dụng gọi video
                    } catch (e: ActivityNotFoundException) {
                        // Bắt ngoại lệ khi không tìm thấy ứng dụng phù hợp
                        Toast.makeText(context, "Thiết bị không hỗ trợ gọi video", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Bắt các ngoại lệ khác có thể xảy ra
                        Toast.makeText(context, "Không thể thực hiện cuộc gọi video", Toast.LENGTH_SHORT).show()
                    }
                }

                // Tạo nút "Mail" với biểu tượng email
                StudentActionButton(icon = Icons.Filled.Email, label = "Mail") {
                    // Tạo Intent để mở ứng dụng email
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${student.email}") // Cấu hình URI với địa chỉ email của sinh viên
                    }
                    context.startActivity(intent) // Khởi chạy ứng dụng email
                }
            }

            // Tạo khoảng trống 24dp theo chiều dọc giữa các nút hành động và thông tin chi tiết
            Spacer(modifier = Modifier.height(24.dp))

            // Hiển thị các trường thông tin chi tiết của sinh viên
            StudentInfoField(label = "Mã sinh viên", value = student.studentID)
            StudentInfoField(label = "Lớp", value = student.className)
            StudentInfoField(label = "Địa chỉ", value = student.address)
            StudentInfoField(label = "Số điện thoại", value = student.phone)
            StudentInfoField(label = "Email", value = student.email)
        }
    }
}

@Composable
fun StudentActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    // Hàm composable tùy chỉnh để tạo nút hành động cho sinh viên
    // - icon: biểu tượng hiển thị trên nút
    // - label: nhãn hiển thị dưới biểu tượng
    // - onClick: lambda function được gọi khi người dùng nhấn nút

    // Sắp xếp biểu tượng và nhãn theo chiều dọc, căn giữa theo chiều ngang
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) { // Tạo nút biểu tượng có thể nhấn, khi nhấn sẽ gọi hàm onClick
            Icon(icon, contentDescription = label, tint = Color(0xFF007AFF)) // Hiển thị biểu tượng với màu xanh iOS (0xFF007AFF) và mô tả cho accessibility
        }
        // Hiển thị nhãn của nút
        Text(
            text = label,
            fontSize = 12.sp, // Kích thước chữ 12sp
            color = Color(0xFF007AFF),  // Màu chữ xanh iOS (0xFF007AFF)
            modifier = Modifier.clickable(onClick = onClick) // Cho phép nhấn vào nhãn để kích hoạt cùng hành động như khi nhấn vào biểu tượng
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoField(label: String, value: String) {
    // Hàm composable tùy chỉnh để hiển thị trường thông tin của sinh viên
    // - label: nhãn của trường thông tin
    // - value: giá trị hiển thị

    // Sắp xếp các thành phần theo chiều dọc, chiếm toàn bộ chiều rộng và thêm padding 4dp theo chiều dọc
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        // Sử dụng OutlinedTextField để tạo trường văn bản có viền
        OutlinedTextField(
            value = value, // Giá trị hiển thị trong trường
            onValueChange = { }, // Hàm rỗng vì trường này chỉ đọc, không cho phép thay đổi giá trị
            modifier = Modifier.fillMaxWidth(),  // Mở rộng để lấp đầy toàn bộ chiều rộng có sẵn
            readOnly = true, // Đặt trường ở chế độ chỉ đọc
            shape = RoundedCornerShape(16.dp), // Bo tròn góc 16dp
            label = { Text(label, color = Color.Gray) }, // Hiển thị nhãn của trường với màu xám
            // Tùy chỉnh màu sắc cho các trạng thái khác nhau của trường
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color(0xFF007AFF),
                unfocusedIndicatorColor = Color(0xFFE0E0E0),
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}