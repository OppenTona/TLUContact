package com.example.tlucontact.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tlucontact.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailStudentScreen(studentId: String, onBack: () -> Unit) {
    val studentViewModel: StudentViewModel = viewModel()
    studentViewModel.setSelectedStudent(studentId)
    val selectedStudent by studentViewModel.selectedStudent.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sinh viên") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Thêm icon quay lại nếu cần
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            selectedStudent?.let { student ->
                Text("Mã sinh viên: ${student.studentID}")
                Text("Tên: ${student.fullNameStudent}")
                Text("Email: ${student.email}")
                Text("Số điện thoại: ${student.phone}")
                Text("Địa chỉ: ${student.address}")
                Text("Lớp: ${student.className}")
                // Hiển thị các thông tin khác của sinh viên
            } ?: Text("Đang tải dữ liệu...")
        }
    }
}