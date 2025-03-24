@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.tlucontact

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.net.Uri
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.app.Activity
import androidx.navigation.NavController
import androidx.compose.material3.*

@Composable
fun DetailScreen(
    navController: NavController,
    name: String,
    studentId: String,
    className: String,
    email: String,
    phone: String,
    address: String,
    notes: String = ""
) {
    val decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8.toString())
    val decodedStudentId = URLDecoder.decode(studentId, StandardCharsets.UTF_8.toString())
    val decodedClassName = URLDecoder.decode(className, StandardCharsets.UTF_8.toString())
    val decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString())
    val decodedPhone = URLDecoder.decode(phone, StandardCharsets.UTF_8.toString())
    val decodedAddress = URLDecoder.decode(address, StandardCharsets.UTF_8.toString())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin sinh viên") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Quay lại màn hình trước
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = rememberAsyncImagePainter("https://i.pravatar.cc/150?u=$decodedEmail"),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = decodedName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(icon = Icons.Default.Message, label = "Tin nhắn")
                ActionButton(icon = Icons.Default.Call, label = "Gọi", phoneNumber = phone)
                ActionButton(icon = Icons.Default.VideoCall, label = "Gọi video")
                ActionButton(icon = Icons.Default.Email, label = "Email")
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoField(label = "Mã sinh viên", value = decodedStudentId, isLink = true)
            InfoField(label = "Lớp", value = decodedClassName)
            InfoField(label = "Số điện thoại", value = decodedPhone)
            InfoField(label = "Email", value = decodedEmail, isLink = true)
            InfoField(label = "Địa chỉ", value = decodedAddress)
            InfoField(label = "Ghi chú", value = notes)
        }
    }
}


@Composable
fun ActionButton(icon: ImageVector, label: String, phoneNumber: String? = null) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {
            if (label == "Gọi" && phoneNumber != null) {
                makePhoneCall(context, phoneNumber)
            }
        }) {
            Icon(icon, contentDescription = label, tint = Color(0xFF007AFF))
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable {
                if (label == "Gọi" && phoneNumber != null) {
                    makePhoneCall(context, phoneNumber)
                }
            }
        )
    }
}


@Composable
fun InfoField(label: String, value: String, isLink: Boolean = false) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = if (isLink) Color.Blue else Color.Black,
                disabledLabelColor = Color.Gray,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Gray
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


