package com.example.tlucontact.view

import android.Manifest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Staff
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class) // Cho phép sử dụng API đang ở giai đoạn thử nghiệm
@Composable
fun DetailContactScreen(staff: Staff, onBack: () -> Unit) {
    val scrollState = rememberScrollState() // Ghi nhớ trạng thái cuộn để dùng trong vertical scroll

    Scaffold( // Scaffold giúp tạo layout có top bar, content và snackbar, v.v.
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cán bộ giảng viên", color = Color.Black) }, // Tiêu đề thanh top bar
                navigationIcon = {
                    IconButton(onClick = onBack) { // Nút quay lại
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White) // Màu nền trắng cho top bar
            )
        }
    ) { paddingValues -> // Nội dung bên trong scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState) // Cho phép cuộn
                .padding(paddingValues)
                .padding(16.dp), // Lề
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các phần tử theo chiều ngang
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = staff.avatarURL), // Hiển thị ảnh đại diện từ URL
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape) // Bo tròn thành hình tròn
            )

            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách

            Text( // Hiển thị tên
                text = staff.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row( // Dòng chứa các nút chức năng
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Giãn đều
            ) {
                ActionButton(icon = Icons.Filled.Chat, label = "Tin nhắn", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.Phone, label = "Gọi", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.VideoCall, label = "Gọi video", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.Email, label = "Mail", phoneNumber = staff.email)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Các trường thông tin tĩnh
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
fun ActionButton(icon: ImageVector, label: String, phoneNumber: String? = null) {
    val context = LocalContext.current // Lấy context để gọi intent

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            // Xử lý hành động tương ứng với nút được nhấn
            when (label) {
                "Gọi" -> phoneNumber?.let { makePhoneCall(context, it) }
                "Tin nhắn" -> phoneNumber?.let { sendSMS(context, it) }
                "Mail" -> phoneNumber?.let { sendEmail(context, it) }
                "Gọi video" -> phoneNumber?.let { openVideoCallApp(context, it) }
            }
        }) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF007AFF))
        }

        Text( // Label dưới nút
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable {
                // Cũng xử lý khi người dùng nhấn vào chữ
                when (label) {
                    "Gọi" -> phoneNumber?.let { makePhoneCall(context, it) }
                    "Tin nhắn" -> phoneNumber?.let { sendSMS(context, it) }
                    "Mail" -> phoneNumber?.let { sendEmail(context, it) }
                    "Gọi video" -> phoneNumber?.let { openVideoCallApp(context, it) }
                }
            }
        )
    }
}


fun sendSMS(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber")) // Mở app SMS với số đã điền
    context.startActivity(intent)
}

fun sendEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email") // Mở app email với email đã điền
    }
    context.startActivity(intent)
}

fun openVideoCallApp(context: Context, phoneNumber: String) {
    val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.meetings") // Gọi Google Meet
    if (intent != null) {
        context.startActivity(intent)
    } else {
        // Nếu chưa cài, mở Google Play để cài
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.meetings"))
        context.startActivity(playStoreIntent)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { }, // Không cho chỉnh sửa
            modifier = Modifier.fillMaxWidth(),
            readOnly = true, // Chỉ hiển thị, không chỉnh sửa
            shape = RoundedCornerShape(16.dp),
            label = { Text(label, color = Color.Gray) }, // Nhãn ở viền ngoài
            colors = TextFieldDefaults.colors( // Màu sắc các trạng thái
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

fun makePhoneCall(context: Context, phoneNumber: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        // Nếu chưa được cấp quyền gọi điện, yêu cầu quyền
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CALL_PHONE),
            1
        )
    } else {
        val intent = Intent(Intent.ACTION_CALL) // Gọi trực tiếp
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }
}
