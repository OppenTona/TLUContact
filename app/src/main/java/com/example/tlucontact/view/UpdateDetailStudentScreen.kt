package com.example.tlucontact.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tlucontact.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailStudentScreen(onBack: () -> Unit, onSave: () -> Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin sinh viên") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ảnh đại diện
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = R.drawable.student),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { /* Xử lý chọn ảnh */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Chỉnh sửa ảnh")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tên
            Text(text = "Nguyễn Văn An", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Các trường thông tin
            StudentEditableField(label = "Mã sinh viên", value = "22510611111", editable = false)
            StudentEditableField(label = "Lớp", value = "61TH1", editable = false)
            StudentEditableField(label = "Số điện thoại", value = "03947646732", editable = true)
            StudentEditableField(label = "Email", value = "annguyen@example.com", editable = false)
            StudentEditableField(label = "Địa chỉ", value = "Hà Nội", editable = true)

            Spacer(modifier = Modifier.height(32.dp))

            // Nút Hủy và Lưu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                    Text("Hủy", color = Color.White)
                }
                Button(onClick = onSave, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                    Text("Lưu", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditableField(label: String, value: String, editable: Boolean) {
    var text by remember { mutableStateOf(value) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { if (editable) text = it },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !editable,
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