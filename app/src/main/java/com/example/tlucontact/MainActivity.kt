package com.example.tlucontact

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tlucontact.data.repository.SessionManager
import com.example.tlucontact.view.ForgotPasswordScreen
import com.example.tlucontact.view.HomeScreen
import com.example.tlucontact.view.LoginScreen
import com.example.tlucontact.view.SignupScreen
import com.example.tlucontact.view.UpdateDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Lấy ngữ cảnh hiện tại (Context) từ Compose để sử dụng trong các lớp khác.
            val context = LocalContext.current
            // Tạo đối tượng SessionManager truyền vào Context để quản lý phiên đăng nhập.
            val sessionManager = SessionManager(context)
            // Kiểm tra trạng thái đăng nhập của người dùng, nếu đã có token lưu trữ thì chuyển về Home, ngược lại là Login.
            val startDestination = if (sessionManager.isLoggedIn()) "Home screen" else "login"
            MyAppNavigation(startDestination)
        }
    }

    @Composable
    fun MyAppNavigation(startDestination: String) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = startDestination) {


            composable(route = "update_detail") {
                UpdateDetailScreen(
                    onBack = { navController.popBackStack() },  // Quay lại màn hình trước
                    onSave = { /* Xử lý lưu thông tin */ }
                )
            }
            composable("login") {
                LoginScreen(navController)
            }
            composable("signup") {
                SignupScreen(navController)
            }
            composable("home") {
                val context = LocalContext.current
                context.startActivity(Intent(context, home::class.java))
            }
            composable("forgotPassword") {
                ForgotPasswordScreen(navController)
            }

            composable("Home screen") {
                val context = LocalContext.current
                context.startActivity(Intent(context, HomeScreen::class.java))
                //context.startActivity(Intent(context, home::class.java))
            }

        }
    }
}
