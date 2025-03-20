package com.example.tlucontact

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tlucontact.Model.User
import com.example.tlucontact.Ulity.UserDatabaseHelper
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileScreen { finish() } // Đóng Activity khi quay lại
        }
    }
}

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val dbHelper = PreferenceHelper(context)
    val userId = dbHelper.getUserId() ?: ""
    val auth = FirebaseAuth.getInstance()


    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance().getReference("users")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            database.child(userId).get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    studentId = it.child("uid").value.toString()
                    className = it.child("position").value.toString()
                    phoneNumber = it.child("phone").value.toString()
                    email = it.child("email").value.toString()
                    name = it.child("name").value.toString()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Chỉnh sửa thông tin",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = rememberVectorPainter(Icons.Default.AccountCircle),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            IconButton(
                onClick = { /* TODO: Thay đổi ảnh */ },
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .size(24.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Avatar", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        InputField(label = "Mã sinh viên", value = studentId, onValueChange = { studentId = it })
        InputField(label = "Lớp", value = className, onValueChange = { className = it })
        InputField(label = "Số điện thoại", value = phoneNumber, onValueChange = { phoneNumber = it })
        InputField(label = "Email", value = email, onValueChange = { email = it })

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onBack() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text(text = "Hủy", color = Color.White)
            }
            Button(
                onClick = {
                    val updatedUser = mapOf(
                        "uid" to studentId,
                        "position" to className,
                        "phone" to phoneNumber,
                        "email" to email,
                        "name" to name
                    )

                    database.child(userId).updateChildren(updatedUser).addOnSuccessListener {
                        Toast.makeText(context, "Thông tin đã lưu", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "Lưu thất bại", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
            ) {
                Text(text = "Lưu", color = Color.White)
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, readOnly: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textStyle = TextStyle(fontSize = 16.sp),
        shape = RoundedCornerShape(8.dp),
        readOnly = readOnly,
        trailingIcon = {
            if (!readOnly) {
                IconButton(onClick = { /* TODO: Xử lý chỉnh sửa */ }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    )
}
