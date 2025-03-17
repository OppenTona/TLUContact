package com.example.testingjetpack.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testingjetpack.R

@Composable
fun SignUp(){
    var fullname by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Đăng ký",
            fontSize = 28.sp,
            color = Color.Black
            //modifier = Modifier.padding(vertical = 16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.thuyloi),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Chỉ mất vài giây để kết nối với mọi người",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = fullname,
            onValueChange = {fullname = it},
            label = { Text("Họ tên")}
        )

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Mật khẩu")},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(onClick = { /*Xử lý đăng ký*/ }) {
            Text(text = "Đăng ký")
        }

        Button(onClick = {/* Xử lý điều hướng đăng nhập */}){
            Text(text = "Đã có tài khoảng? Đăng nhập", color = Color.Blue)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUp(){
    SignUp()
}