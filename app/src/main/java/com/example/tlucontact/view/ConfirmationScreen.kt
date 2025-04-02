package com.example.tlucontact.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tlucontact.R

@Composable
fun ConfirmationScreen(
    email: String,
    onConfirmClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E5E5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.thuyloi), // Thay bằng logo của bạn
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chúng tôi đã gửi email xác nhận đến",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = email,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hãy xác nhận email trong hộp thư của bạn",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onConfirmClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Tôi đã xác nhận", color = Color.White)
            }
        }
    }
}