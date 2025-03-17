package com.example.testingjetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testingjetpack.ui.theme.TestingJetPackTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testingjetpack.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignUp()
        }
    }
}

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

        androidx.compose.material.Text(
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

        androidx.compose.material.Text(
            text = "Chỉ mất vài giây để kết nối với mọi người",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = fullname,
            onValueChange = {fullname = it},
            label = { androidx.compose.material.Text("Họ tên") }
        )

        BasicTextField(
            value = email,
            onValueChange = {email = it},
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black), // Định dạng chữ
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Căn chỉnh kích thước và khoảng cách
            singleLine = true,
            decorationBox = { innerTextField ->
                Column{
                    //Text(text = "Email", color = Color.Gray, fontSize = 14.sp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Box(modifier = Modifier.weight(1f)){
                            if(email.isEmpty()){
                                Text(text = "Email", color = Color.Gray, fontSize = 16.sp)
                            }
                        }
                    }
                    innerTextField()
                }
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { androidx.compose.material.Text("Mật khẩu") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(onClick = { /*Xử lý đăng ký*/ }) {
            androidx.compose.material.Text(text = "Đăng ký")
        }

        Button(onClick = {/* Xử lý điều hướng đăng nhập */}){
            androidx.compose.material.Text(text = "Đã có tài khoảng? Đăng nhập", color = Color.Blue)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUp(){
    TestingJetPackTheme {
        SignUp()
    }
}