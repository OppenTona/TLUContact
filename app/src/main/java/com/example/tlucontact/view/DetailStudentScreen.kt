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
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin sinh viên", color = Color.Black) },
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
                painter = rememberAsyncImagePainter(student.photoURL.ifEmpty { "https://i.pravatar.cc/150?u=${student.email}" }),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = student.fullNameStudent, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StudentActionButton(icon = Icons.Filled.Chat, label = "Tin nhắn") {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("sms:${student.phone}")
                    }
                    context.startActivity(intent)
                }

                StudentActionButton(icon = Icons.Filled.Phone, label = "Gọi") {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${student.phone}")
                    }
                    context.startActivity(intent)
                }

                StudentActionButton(icon = Icons.Filled.VideoCall, label = "Gọi video") {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("tel:${student.phone}")
                        }
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Thiết bị không hỗ trợ gọi video", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Không thể thực hiện cuộc gọi video", Toast.LENGTH_SHORT).show()
                    }
                }

                StudentActionButton(icon = Icons.Filled.Email, label = "Mail") {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${student.email}")
                    }
                    context.startActivity(intent)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label, tint = Color(0xFF007AFF))
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoField(label: String, value: String) {
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