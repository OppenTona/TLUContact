package com.example.tlucontact.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tlucontact.R
import com.example.tlucontact.viewmodel.LoginViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()
    val email by viewModel.email.collectAsState()

    val resetState by viewModel.resetState.collectAsState()

    LaunchedEffect(resetState) {
        if (resetState.first) {
            Toast.makeText(context, resetState.second ?: "Success", Toast.LENGTH_LONG).show()
            navController.popBackStack()
        } else if (resetState.second != null) {
            Toast.makeText(context, "Lỗi: ${resetState.second}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.staff_icon), // Thêm icon vào drawable
//                    contentDescription = "Back",
//                    tint = Color.Black
//                )
//            }
//        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("Gửi mã xác minh", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.thuyloi), // Đặt logo của bạn vào drawable và sửa tên phù hợp
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = {Text("Nhập email của bạn")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                disabledIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.resetPassword(email)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black), // Màu xanh Microsoft
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Gửi mã xác minh", color = Color.White, fontSize = 16.sp)
        }
    }
}