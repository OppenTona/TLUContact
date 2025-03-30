package com.example.tlucontact.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tlucontact.viewmodel.ConfirmEmailViewModel
import com.example.tlucontact.viewmodel.ConfirmStatus

@Composable
fun ConfirmEmailScreen(
    navController: NavController,
    email: String,
    name: String,
    password: String,
    viewModel: ConfirmEmailViewModel = hiltViewModel()
) {
    // Gán các tham số truyền từ route vào ViewModel.
    viewModel.email = email
    viewModel.password = password

    val confirmStatus = viewModel.confirmStatus.collectAsState()

    // Giao diện của màn hình xác nhận email.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Đã gửi email tới $email, vui lòng kiểm tra hộp thư và xác nhận email của bạn.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.checkEmailVerification() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tôi đã xác nhận")
        }
    }

    LaunchedEffect(confirmStatus.value) {
        when (confirmStatus.value) {
            is ConfirmStatus.Verified -> {
                // Sau khi tài khoản được tạo (email xác nhận thành công), chuyển hướng về màn hình đăng nhập.
                navController.navigate("login") {
                    popUpTo("confirmEmail") { inclusive = true }
                }
            }
            is ConfirmStatus.NotVerified -> {
                // Có thể hiển thị thông báo cho người dùng: "Email chưa được xác nhận" (ví dụ Toast)
            }
            is ConfirmStatus.Error -> {
                // Xử lý thông báo lỗi ở đây (ví dụ hiển thị message trên UI)
            }
            else -> Unit
        }
    }
}