package com.example.tlucontact.view

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tlucontact.R // Nhập R để truy cập tài nguyên drawable
import com.example.tlucontact.data.model.Department

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentDetailView(department: Department, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn vị", color = Color.Black) },
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
                painter = painterResource(id = R.drawable.thuyloi), // Sử dụng painterResource
                contentDescription = "Ảnh đơn vị",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = department.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center, // Căn giữa văn bản
                modifier = Modifier.fillMaxWidth() // Cho phép Text chiếm toàn bộ chiều rộng
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DepartmentActionButton(icon = Icons.Filled.Chat, label = "tin nhắn")
                DepartmentActionButton(icon = Icons.Filled.Phone, label = "gọi")
                DepartmentActionButton(icon = Icons.Filled.VideoCall, label = "gọi video")
                DepartmentActionButton(icon = Icons.Filled.Email, label = "mail")
            }

            Spacer(modifier = Modifier.height(24.dp))

            DepartmentInfoField(label = "Tên đơn vị", value = department.name)
            DepartmentInfoField(label = "Mã đơn vị", value = department.id)
            DepartmentInfoField(label = "Trưởng đơn vị", value = department.leader)
            DepartmentInfoField(label = "Email", value = department.email)
            DepartmentInfoField(label = "Số điện thoại", value = department.phone)
            DepartmentInfoField(label = "Địa chỉ", value = department.address)
            DepartmentInfoField(label = "Ghi chú", value = "-")
        }
    }
}

@Composable
fun DepartmentActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { /* Xử lý sự kiện */ }) {
            Icon(icon, contentDescription = label, tint = Color(0xFF007AFF))
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable { /* Xử lý sự kiện */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentInfoField(label: String, value: String) {
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
