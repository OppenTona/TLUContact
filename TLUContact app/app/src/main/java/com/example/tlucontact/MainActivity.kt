package com.example.tlucontact  // Định nghĩa tên gói cho ứng dụng này

import android.os.Bundle  // Nhập lớp Bundle được sử dụng để lưu trạng thái của activity
import androidx.activity.ComponentActivity  // Nhập lớp cơ sở cho các activity sử dụng Jetpack Compose
import androidx.activity.compose.setContent  // Nhập hàm để thiết lập nội dung Compose trong activity
import androidx.compose.runtime.Composable  // Nhập chú thích để đánh dấu các hàm là thành phần UI Compose
import androidx.compose.ui.platform.LocalContext  // Nhập provider để truy cập ngữ cảnh Android trong Compose
import androidx.navigation.compose.NavHost  // Nhập container để điều hướng giữa các điểm đến composable
import androidx.navigation.compose.composable  // Nhập hàm để định nghĩa điểm đến composable trong điều hướng
import androidx.navigation.compose.rememberNavController  // Nhập hàm để tạo và ghi nhớ NavController
import com.example.tlucontact.data.repository.SessionManager  // Nhập lớp tùy chỉnh để quản lý dữ liệu phiên người dùng
import com.example.tlucontact.view.ForgotPasswordScreen  // Nhập màn hình quên mật khẩu composable
import com.example.tlucontact.view.HomeScreen  // Nhập màn hình chính composable
import com.example.tlucontact.view.LogInScreen  // Nhập màn hình đăng nhập composable
import com.example.tlucontact.view.SignUpScreen  // Nhập màn hình đăng ký composable

class MainActivity : ComponentActivity() {  // Định nghĩa lớp activity chính kế thừa từ ComponentActivity
    override fun onCreate(savedInstanceState: Bundle?) {  // Ghi đè phương thức onCreate với tham số trạng thái đã lưu
        super.onCreate(savedInstanceState)  // Gọi phương thức onCreate của lớp cha để khởi tạo activity đúng cách
        setContent {  // Thiết lập nội dung Compose cho activity này
            // Lấy ngữ cảnh hiện tại (Context) từ Compose để sử dụng trong các lớp khác.
            val context = LocalContext.current  // Lấy ngữ cảnh hiện tại từ môi trường Compose
            // Tạo đối tượng SessionManager truyền vào Context để quản lý phiên đăng nhập.
            val sessionManager = SessionManager(context)  // Tạo thể hiện SessionManager truyền vào ngữ cảnh để quản lý phiên đăng nhập
            // Kiểm tra trạng thái đăng nhập của người dùng, nếu đã có token lưu trữ thì chuyển về Home, ngược lại là Login.
            val startDestination = if (sessionManager.isLoggedIn()) "Home screen" else "login"  // Xác định điểm đến bắt đầu dựa trên trạng thái đăng nhập
            MyAppNavigation(startDestination)  // Gọi hàm thiết lập điều hướng với điểm đến đã xác định
        }
    }

    @Composable  // Đánh dấu hàm này là một thành phần UI Composable
    fun MyAppNavigation(startDestination: String) {  // Định nghĩa hàm điều hướng với tham số cho điểm đến bắt đầu
        val navController = rememberNavController()  // Tạo và ghi nhớ NavController để quản lý điều hướng

        NavHost(navController = navController, startDestination = startDestination) {  // Thiết lập NavHost với bộ điều khiển và điểm bắt đầu

            composable("login") {  // Định nghĩa điểm đến "login"
                LogInScreen(navController)  // Sử dụng composable LogInScreen và truyền navController để điều hướng
            }

            composable("signup") {  // Định nghĩa điểm đến "signup"
                SignUpScreen(navController)  // Sử dụng composable SignUpScreen và truyền navController để điều hướng
            }

            composable("forgotPassword") {  // Định nghĩa điểm đến "forgotPassword"
                ForgotPasswordScreen(navController)  // Sử dụng composable ForgotPasswordScreen và truyền navController để điều hướng
            }

            composable("Home screen") {  // Định nghĩa điểm đến "Home screen"
                HomeScreen(navController) // Sử dụng HomeScreen dưới dạng hàm  // Sử dụng composable HomeScreen và truyền navController để điều hướng
            }
        }
    }
}