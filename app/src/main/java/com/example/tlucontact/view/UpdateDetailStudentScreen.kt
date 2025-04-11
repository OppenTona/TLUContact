package com.example.tlucontact.view

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.viewmodel.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.Activity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tlucontact.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailStudentScreen(
    student: Student?,
    onBack: () -> Unit, // Hàm xử l ý khi nhấn nút quay lại
    onSave: (Student) -> Unit, // Hàm xử lý khi nhấn nút lưu
    viewModel: StudentViewModel, // ViewModel để cập nhật thông tin sinh viên
    navController: NavController // NavController để điều hướng màn hinh
) {
    val scrollState = rememberScrollState() // Trạng thái cuộn áp dụng cho Column có thể cuộn dọc

    // Các biến trạng thái để lưu thông tin sinh viên
    var fullName by remember { mutableStateOf(student?.fullNameStudent ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }

    // SnackbarHostState để hiển thị thông báo thành công
    val snackbarHostState = remember { SnackbarHostState() }

    // Lấy context và activity từ LocalContext
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        // Thiết lập màu nền cho Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        // Thanh tiêu đề và nút quay lại
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (student != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize() // Chiếm toàn bộ chiều rộng và chiều cao
                    .verticalScroll(scrollState) // Cuộn dọc
                    .padding(paddingValues) // Padding từ Scaffold
                    .padding(16.dp), // Padding bên trong
                horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các phần tử theo chiều ngang
            ) {
                // Ảnh và tên hồ sơ
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = rememberAsyncImagePainter(model = student.photoURL),
                        contentDescription = "Ảnh đại diện", // Ảnh đại diện
                        modifier = Modifier
                            .size(100.dp) // Kích thước ảnh
                            .clip(CircleShape), // Hình dạng tròn
                        contentScale = ContentScale.Crop // Cắt ảnh theo hình tròn
                    )
                    IconButton(
                        onClick = { /* Handle image selection */ },
                        modifier = Modifier.size(24.dp) // Kích thước nút
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Chỉnh sửa ảnh") // Nút chỉnh sửa ảnh
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa ảnh và tên
                Text(text = student.fullNameStudent, fontSize = 20.sp) // Tên sinh viên
                Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách giữa tên và các trường thông tin

                // Các trường thông tin sinh viên, (studentID, lớp, email) không thể chỉnh sửa, (địa chỉ, số điện thoại) có thể chỉnh sửa
                StudentEditableField(label = "Mã sinh viên", value = student.studentID, onValueChange = {}, editable = false)
                StudentEditableField(label = "Lớp", value = student.className, onValueChange = {}, editable = false)
                StudentEditableField(label = "Số điện thoại", value = phone, onValueChange = { phone = it }, editable = true)
                StudentEditableField(label = "Email", value = student.email, onValueChange = {}, editable = false)
                StudentEditableField(label = "Địa chỉ nơi ở", value = address, onValueChange = { address = it }, editable = true)

                Spacer(modifier = Modifier.height(32.dp)) // Khoảng cách giữa các trường thông tin và nút lưu

                Row(
                    modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
                    horizontalArrangement = Arrangement.SpaceEvenly // Căn giữa các nút theo chiều ngang
                ) {
                    // Nút hủy và lưu
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Hủy", color = Color.White)
                    }
                    Button(onClick = {
                        // Cập nhật thông tin sinh viên
                        val updatedStudent = student.copy(
                            fullNameStudent = fullName,
                            phone = phone,
                            address = address
                        )
                        viewModel.updateStudentInfo(updatedStudent)
                        // Hiển thị thông báo
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Cập nhật thông tin thành công")
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Lưu", color = Color.White)
                    }

                }
            }
        } else {
            // Nếu không có thông tin sinh viên, hiển thị một thông báo
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditableField(
    label: String, // Tên trường
    value: String, // Giá trị của trường
    onValueChange: (String) -> Unit, // Hàm xử lý khi giá trị thay đổi
    editable: Boolean // Trạng thái có thể chỉnh sửa hay không
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        // Trường thông tin sinh viên
        OutlinedTextField(
            value = value,
            onValueChange = { if (editable) onValueChange(it) }, // Hàm xử lý khi giá trị thay đổi
            modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
            readOnly = !editable, // Trường có thể chỉnh sửa hay không
            shape = RoundedCornerShape(16.dp), // Hình dạng bo tròn
            label = { Text(label, color = Color.Gray) }, // Nhãn của trường
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
