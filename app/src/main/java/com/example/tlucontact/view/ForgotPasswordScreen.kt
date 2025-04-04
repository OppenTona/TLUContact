package com.example.tlucontact.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(resetState) {
        if (resetState.first) {
            Toast.makeText(context, resetState.second ?: "Success", Toast.LENGTH_LONG).show()
            navController.navigate("login")
        } else if (resetState.second != null) {
            Toast.makeText(context, "Lỗi: ${resetState.second}", Toast.LENGTH_LONG).show()
        }
    }

    if (isLandscape) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ForgotPasswordLogo()
                }
                Spacer(modifier = Modifier.width(32.dp))
                Box(modifier = Modifier.weight(1f)) {
                    ForgotPasswordForm(navController, viewModel, email)
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ForgotPasswordLogo()
                Spacer(modifier = Modifier.height(16.dp))
                ForgotPasswordForm(navController, viewModel, email)
            }
        }
    }
}

@Composable
fun ForgotPasswordLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Gửi mã xác minh", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.thuyloi),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
fun ForgotPasswordForm(navController: NavController, viewModel: LoginViewModel, email: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            label = { Text("Nhập email của bạn") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.resetPassword(email) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Gửi mã xác minh", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun BackButton(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }
}
