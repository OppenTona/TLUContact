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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContactScreen(staff: Staff, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cán bộ giảng viên", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = staff.avatarURL),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = staff.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(icon = Icons.Filled.Chat, label = "Tin nhắn", phoneNumber = staff.phone) // Assuming you might want to handle messaging later
                ActionButton(icon = Icons.Filled.Phone, label = "Gọi", phoneNumber = staff.phone)
                ActionButton(icon = Icons.Filled.VideoCall, label = "Gọi video", phoneNumber = staff.phone)

                ActionButton(icon = Icons.Filled.Email, label = "Mail", phoneNumber = staff.email) // Assuming you might want to handle email later
            }

            Spacer(modifier = Modifier.height(24.dp))

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
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            when (label) {
                "Gọi" -> phoneNumber?.let { makePhoneCall(context, it) }
                "Tin nhắn" -> phoneNumber?.let { sendSMS(context, it) }
                "Mail" -> phoneNumber?.let { sendEmail(context, it) }
                "Gọi video" -> phoneNumber?.let { openVideoCallApp(context, it) }
            }
        }) {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF007AFF))
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable {
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
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
    context.startActivity(intent)
}
fun sendEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
    }
    context.startActivity(intent)
}
fun openVideoCallApp(context: Context, phoneNumber: String) {
    // Mặc định mở Google Meet (có thể thay package name tùy app gọi video bạn hỗ trợ)
    val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.meetings")
    if (intent != null) {
        context.startActivity(intent)
    } else {
        // Nếu không có app, mở Play Store để cài
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
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            label = { Text(label, color = Color.Gray) },
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
fun makePhoneCall(context: Context, phoneNumber: String) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        // Yêu cầu quyền nếu chưa được cấp
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CALL_PHONE),
            1
        )
    } else {
        // Nếu đã có quyền, thực hiện cuộc gọi
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }
}