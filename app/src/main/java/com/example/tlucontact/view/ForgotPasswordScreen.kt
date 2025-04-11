package com.example.tlucontact.view // Định nghĩa package của file này, dùng để tổ chức mã nguồn theo cấu trúc thư mục

import android.widget.Toast // Import lớp Toast để hiển thị thông báo ngắn trên màn hình
import androidx.compose.foundation.Image // Import thành phần Image của Jetpack Compose
import androidx.compose.foundation.layout.* // Import các thành phần bố cục (Column, Row, Spacer, Box, v.v.)
import androidx.compose.foundation.shape.RoundedCornerShape // Import để tạo các hình dạng bo tròn
import androidx.compose.foundation.text.KeyboardOptions // Import để cấu hình bàn phím
import androidx.compose.material.* // Import các thành phần Material Design (Button, TextField, v.v.)
import androidx.compose.material.icons.Icons // Import đối tượng biểu tượng (Icons)
import androidx.compose.material.icons.filled.ArrowBack // Import biểu tượng mũi tên quay lại
import androidx.compose.runtime.* // Import các thành phần trạng thái của Jetpack Compose
import androidx.compose.ui.Alignment // Import để căn chỉnh các thành phần
import androidx.compose.ui.Modifier // Import để sửa đổi giao diện thành phần
import androidx.compose.ui.graphics.Color // Import để sử dụng màu sắc
import androidx.compose.ui.platform.LocalConfiguration // Import để lấy cấu hình thiết bị (như chế độ ngang/dọc)
import androidx.compose.ui.platform.LocalContext // Import để lấy ngữ cảnh hiện tại
import androidx.compose.ui.res.painterResource // Import để sử dụng tài nguyên hình ảnh
import androidx.compose.ui.text.font.FontWeight // Import để thiết lập trọng lượng font chữ
import androidx.compose.ui.text.input.KeyboardType // Import để thiết lập kiểu bàn phím
import androidx.compose.ui.unit.dp // Import để thiết lập kích thước theo đơn vị dp
import androidx.compose.ui.unit.sp // Import để thiết lập kích thước font chữ theo đơn vị sp
import androidx.lifecycle.viewmodel.compose.viewModel // Import để lấy ViewModel thông qua Compose
import androidx.navigation.NavController // Import để điều hướng giữa các màn hình
import com.example.tlucontact.R // Import tài nguyên từ tệp R (như hình ảnh, chuỗi, v.v.)
import com.example.tlucontact.viewmodel.LoginViewModel // Import ViewModel để sử dụng logic liên quan đến đăng nhập

@Composable // Đánh dấu hàm là một thành phần giao diện trong Compose
fun ForgotPasswordScreen(navController: NavController) { // Hàm hiển thị màn hình quên mật khẩu, nhận đối tượng điều hướng làm tham số
    val context = LocalContext.current // Lấy ngữ cảnh hiện tại để sử dụng cho Toast
    val viewModel: LoginViewModel = viewModel() // Lấy LoginViewModel để quản lý trạng thái
    val email by viewModel.email.collectAsState() // Lấy trạng thái email từ ViewModel
    val emailError by viewModel.emailError.collectAsState()
    val resetState by viewModel.resetState.collectAsState() // Lấy trạng thái reset mật khẩu từ ViewModel
    val configuration = LocalConfiguration.current // Lấy cấu hình hiện tại của thiết bị
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE // Kiểm tra thiết bị có đang ở chế độ ngang hay không

    LaunchedEffect(resetState) { // Theo dõi trạng thái resetState, thực hiện hành động khi trạng thái thay đổi
        if (resetState.first) { // Nếu reset thành công
            Toast.makeText(context, resetState.second ?: "Success", Toast.LENGTH_LONG).show() // Hiển thị thông báo thành công
            navController.navigate("login") // Điều hướng đến màn hình đăng nhập
        } else if (resetState.second != null) { // Nếu có lỗi trong quá trình reset
            Toast.makeText(context, "Lỗi: ${resetState.second}", Toast.LENGTH_LONG).show() // Hiển thị thông báo lỗi
        }
    }

    if (isLandscape) { // Nếu thiết bị ở chế độ ngang
        Column( // Sử dụng bố cục cột
            modifier = Modifier
                .fillMaxSize() // Chiếm toàn bộ kích thước màn hình
                .padding(16.dp) // Thêm khoảng cách padding 16dp
        ) {
            BackButton(navController) // Hiển thị nút quay lại
            Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dọc 16dp
            Row( // Sử dụng bố cục hàng
                modifier = Modifier.weight(1f), // Chiếm không gian còn lại
                verticalAlignment = Alignment.CenterVertically // Căn giữa theo chiều dọc
            ) {
                Box( // Hộp chứa thành phần
                    modifier = Modifier.weight(1f), // Chiếm không gian còn lại
                    contentAlignment = Alignment.Center // Căn giữa nội dung
                ) {
                    ForgotPasswordLogo() // Hiển thị logo quên mật khẩu
                }
                Spacer(modifier = Modifier.width(32.dp)) // Thêm khoảng cách ngang 32dp
                Box(modifier = Modifier.weight(1f)) { // Hộp chứa form quên mật khẩu
                    ForgotPasswordForm(navController, viewModel, email, emailError) // Hiển thị form quên mật khẩu
                }
            }
        }
    } else { // Nếu thiết bị ở chế độ dọc
        Column( // Sử dụng bố cục cột
            modifier = Modifier
                .fillMaxSize() // Chiếm toàn bộ kích thước màn hình
                .padding(16.dp), // Thêm khoảng cách padding 16dp
            horizontalAlignment = Alignment.Start // Căn chỉnh các thành phần về bên trái
        ) {
            BackButton(navController) // Hiển thị nút quay lại
            Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dọc 16dp
            Column( // Sử dụng bố cục cột bên trong
                horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
                modifier = Modifier.fillMaxWidth() // Chiếm toàn bộ chiều rộng
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dọc 16dp
                ForgotPasswordLogo() // Hiển thị logo quên mật khẩu
                Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dọc 16dp
                ForgotPasswordForm(navController, viewModel, email, emailError) // Hiển thị form quên mật khẩu
            }
        }
    }
}

@Composable // Đánh dấu hàm là một thành phần giao diện trong Compose
fun ForgotPasswordLogo() { // Hàm hiển thị logo và tiêu đề trên màn hình quên mật khẩu
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Sử dụng bố cục cột, căn giữa nội dung
        Text("Gửi mã xác minh", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Hiển thị tiêu đề với font chữ lớn và đậm
        Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng cách dọc 8dp
        Image( // Hiển thị hình ảnh logo
            painter = painterResource(id = R.drawable.thuyloi), // Lấy hình ảnh từ tài nguyên
            contentDescription = "Logo", // Mô tả nội dung hình ảnh
            modifier = Modifier.size(100.dp) // Kích thước hình ảnh 100dp
        )
    }
}

@Composable // Đánh dấu hàm là một thành phần giao diện trong Compose
fun ForgotPasswordForm(navController: NavController, viewModel: LoginViewModel, email: String, emailError: Boolean) { // Hàm hiển thị form quên mật khẩu
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Sử dụng bố cục cột, căn giữa nội dung
        TextField( // Trường nhập liệu email
            value = email, // Giá trị của trường nhập liệu
            onValueChange = { viewModel.email.value = it }, // Cập nhật giá trị email trong ViewModel khi người dùng nhập
            label = { Text("Nhập email của bạn") }, // Nhãn cho trường nhập liệu
            singleLine = true, // Chỉ cho phép nhập một dòng
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Thiết lập bàn phím cho kiểu nhập email
            modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
            colors = TextFieldDefaults.textFieldColors( // Thiết lập màu sắc cho trường nhập liệu
                backgroundColor = Color.Transparent, // Nền trong suốt
                unfocusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu khi không được chọn
                focusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu khi được chọn
                focusedLabelColor = if (emailError) Color.Red else Color.Gray // Màu của nhãn khi được chọn
            )
        )
        Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách dọc 16dp
        Button( // Nút gửi mã xác minh
            onClick = { viewModel.resetPassword(email) }, // Gọi hàm resetPassword trong ViewModel khi nhấn nút
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black), // Thiết lập màu nền nút là màu đen
            modifier = Modifier
                .fillMaxWidth() // Chiếm toàn bộ chiều rộng
                .height(50.dp), // Chiều cao nút là 50dp
            shape = RoundedCornerShape(8.dp) // Bo góc nút với bán kính 8dp
        ) {
            Text("Gửi mã xác minh", color = Color.White, fontSize = 16.sp) // Hiển thị văn bản trên nút
        }
    }
}

@Composable // Đánh dấu hàm là một thành phần giao diện trong Compose
fun BackButton(navController: NavController) { // Hàm hiển thị nút quay lại
    Row( // Sử dụng bố cục hàng
        modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
        verticalAlignment = Alignment.CenterVertically // Căn giữa theo chiều dọc
    ) {
        IconButton(onClick = { navController.popBackStack() }) { // Nút biểu tượng, quay lại màn hình trước đó khi nhấn
            Icon( // Hiển thị biểu tượng mũi tên quay lại
                imageVector = Icons.Filled.ArrowBack, // Sử dụng biểu tượng mũi tên quay lại
                contentDescription = "Back", // Mô tả biểu tượng
                tint = Color.Black // Màu biểu tượng là màu đen
            )
        }
    }
}