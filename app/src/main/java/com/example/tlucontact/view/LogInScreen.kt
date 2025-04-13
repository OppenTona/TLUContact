// Khai báo package của lớp
package com.example.tlucontact.view

// Import các thư viện cần thiết
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tlucontact.R
import com.example.tlucontact.viewmodel.LogInViewModel

// Hàm giao diện chính LoginScreen, được gọi để hiển thị màn hình đăng nhập
@Composable
fun LogInScreen(navController: NavController) {
    val context = LocalContext.current // Lấy context hiện tại để sử dụng trong giao diện
    val viewModel: LogInViewModel = viewModel() // Tạo hoặc lấy ViewModel để quản lý logic và trạng thái
    var passwordVisible by remember { mutableStateOf(false) } // Biến trạng thái để theo dõi việc hiển thị mật khẩu

    val loginState by viewModel.loginState.collectAsState() // Lấy trạng thái đăng nhập từ ViewModel
    val configuration = LocalConfiguration.current // Lấy thông tin cấu hình hiện tại của thiết bị (như chế độ ngang/dọc)

    // Lắng nghe trạng thái đăng nhập để hiển thị thông báo hoặc điều hướng
    LaunchedEffect(loginState) {
        if (loginState.first) {
            // Nếu đăng nhập thành công
            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show() // Hiển thị thông báo thành công
            navController.navigate("Home screen") // Điều hướng tới màn hình chính
        } else if (loginState.second != null) {
            // Nếu có lỗi xảy ra
            Toast.makeText(context, "Lỗi: ${loginState.second}", Toast.LENGTH_SHORT).show() // Hiển thị thông báo lỗi
        }
    }

    // Kiểm tra thiết bị đang ở chế độ màn hình ngang hay dọc
    if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        // Chế độ ngang: hiển thị giao diện chia làm hai phần (ảnh bên trái, form bên phải)
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp), // Căn chỉnh và thêm khoảng cách
            verticalAlignment = Alignment.CenterVertically // Căn giữa theo chiều dọc
        ) {
            // Phần ảnh/logo nằm bên trái
            Box(
                modifier = Modifier
                    .weight(1f) // Chiếm 1 phần trọng số
                    .fillMaxHeight() // Chiều cao đầy đủ
                    .verticalScroll(rememberScrollState()), // Cho phép cuộn dọc nếu nội dung vượt quá
                contentAlignment = Alignment.Center // Căn giữa nội dung
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { // Sắp xếp nội dung theo cột
                    Text("Đăng nhập", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Tiêu đề chính
                    Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa các phần tử

                    Image(
                        painter = painterResource(id = R.drawable.thuyloi), // Ảnh logo
                        contentDescription = "Logo", // Mô tả cho ảnh
                        modifier = Modifier.size(100.dp) // Kích thước của logo
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa logo và mô tả
                    Text("Chào mừng đến với TLUContract", fontSize = 14.sp, color = Color.Gray) // Mô tả chào mừng
                }
            }

            Spacer(modifier = Modifier.width(32.dp)) // Khoảng cách giữa phần logo và form

            // Phần form đăng nhập nằm bên phải
            Box(modifier = Modifier
                .weight(1f) // Chiếm 1 phần trọng số
                .fillMaxHeight() // Chiều cao đầy đủ
                .verticalScroll(rememberScrollState()) // Cho phép cuộn dọc
            ) {
                // Gọi hàm hiển thị form đăng nhập
                LoginForm(viewModel, passwordVisible, navController) { passwordVisible = it }
            }
        }
    } else {
        // Chế độ dọc: hiển thị giao diện theo chiều cột
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp), // Căn chỉnh và thêm khoảng cách
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Khoảng cách trên cùng
            Text("Đăng nhập", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Tiêu đề chính
            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa các phần tử

            Image(
                painter = painterResource(id = R.drawable.thuyloi), // Ảnh logo
                contentDescription = "Logo", // Mô tả cho ảnh
                modifier = Modifier.size(80.dp) // Kích thước của logo
            )

            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa logo và mô tả
            Text("Chào mừng đến với TLUContract", fontSize = 14.sp, color = Color.Gray) // Mô tả chào mừng
            Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách trước form

            // Gọi hàm hiển thị form đăng nhập
            LoginForm(viewModel, passwordVisible, navController) { passwordVisible = it }
        }
    }
}

// Hàm hiển thị form đăng nhập
@Composable
fun LoginForm(
    viewModel: LogInViewModel, // ViewModel để quản lý dữ liệu và logic
    passwordVisible: Boolean, // Biến trạng thái hiển thị mật khẩu
    navController: NavController, // Đối tượng điều hướng
    onPasswordVisibilityChange: (Boolean) -> Unit // Hàm callback để thay đổi trạng thái hiển thị mật khẩu
) {
    val email by viewModel.email.collectAsState() // Lấy giá trị email từ ViewModel
    val password by viewModel.password.collectAsState() // Lấy giá trị mật khẩu từ ViewModel
    val emailError by viewModel.emailError.collectAsState() // Lấy trạng thái lỗi của email
    val passwordError by viewModel.passwordError.collectAsState() // Lấy trạng thái lỗi của mật khẩu
    val context = LocalContext.current // Lấy context hiện tại
    val activity = context as? Activity // Ép kiểu context sang Activity (nếu có)

    Column { // Sắp xếp các thành phần trong form theo cột
        // Trường nhập email
        TextField(
            value = email, // Giá trị email
            onValueChange = { viewModel.email.value = it }, // Cập nhật giá trị email trong ViewModel
            label = { Text("Email", color = if (emailError) Color.Red else Color.Gray) }, // Hiển thị nhãn và màu sắc tùy thuộc vào trạng thái lỗi
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent, // Nền trong suốt
                unfocusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu khi không được chọn
                focusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu khi được chọn
                focusedLabelColor = if (emailError) Color.Red else Color.Gray // Màu của nhãn khi được chọn
            ),
            modifier = Modifier.fillMaxWidth() // Chiều rộng đầy đủ
        )

        Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa các trường nhập

        // Trường nhập mật khẩu
        TextField(
            value = password, // Giá trị mật khẩu
            onValueChange = { viewModel.password.value = it }, // Cập nhật giá trị mật khẩu trong ViewModel
            label = { Text("Mật khẩu", color = if (passwordError) Color.Red else Color.Gray) }, // Hiển thị nhãn và màu sắc tùy thuộc vào trạng thái lỗi
            trailingIcon = { // Biểu tượng ở cuối trường nhập
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) { // Nút hiển thị/ẩn mật khẩu
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, // Biểu tượng thay đổi tùy thuộc vào trạng thái
                        contentDescription = "Toggle Password" // Mô tả cho biểu tượng
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Hiển thị hoặc ẩn mật khẩu
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Tùy chọn bàn phím là mật khẩu
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent, // Nền trong suốt
                unfocusedIndicatorColor = if (passwordError) Color.Red else Color.Gray, // Màu khi không được chọn
                focusedIndicatorColor = if (passwordError) Color.Red else Color.Gray, // Màu khi được chọn
                focusedLabelColor = if (passwordError) Color.Red else Color.Gray // Màu của nhãn khi được chọn
            ),
            modifier = Modifier.fillMaxWidth() // Chiều rộng đầy đủ
        )

        Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa các thành phần

        // Nút "Quên mật khẩu"
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { // Sắp xếp nút ở góc phải
            Text(
                "Quên mật khẩu?", // Văn bản của nút
                color = Color.Blue, // Màu sắc
                modifier = Modifier.clickable { navController.navigate("forgotpassword") } // Điều hướng tới màn hình "Quên mật khẩu"
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách trước nút đăng nhập

        // Nút "Đăng nhập"
        Button(
            onClick = { viewModel.login(email, password) }, // Gọi hàm đăng nhập khi nhấn nút
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black), // Màu nền của nút
            modifier = Modifier.fillMaxWidth().height(50.dp), // Chiều rộng đầy đủ và chiều cao 50dp
            shape = RoundedCornerShape(8.dp) // Bo góc nút
        ) {
            Text("Đăng nhập", color = Color.White, fontSize = 16.sp) // Văn bản của nút
        }

        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách trước dòng "Không có tài khoản?"

        // Văn bản điều hướng tới màn hình "Đăng ký"
        Row(
            modifier = Modifier.fillMaxWidth(), // Chiều rộng đầy đủ
            horizontalArrangement = Arrangement.Center // Căn giữa
        ) {
            Text("Không có tài khoản? ") // Văn bản mô tả
            Text(
                "Đăng ký", // Văn bản liên kết
                color = Color.Blue, // Màu xanh
                modifier = Modifier.clickable {
                    navController.navigate("signup") // Điều hướng tới màn hình "Đăng ký"
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Khoảng cách trước Divider

        DividerWithText(text = "hoặc đăng nhập với") // Dòng ngăn cách có văn bản "hoặc đăng nhập với"

        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách trước nút Outlook

        // Nút "Đăng nhập bằng Outlook"
        Button(
            onClick = {
                activity?.let { act -> // Kiểm tra nếu Activity không null
                    viewModel.loginWithMicrosoft(act) { success, error -> // Gọi hàm đăng nhập bằng Microsoft
                        // Kết quả được xử lý bởi trạng thái loginState trong ViewModel
                    }
                } ?: Toast.makeText(context, "Không xác định được Activity", Toast.LENGTH_SHORT).show() // Thông báo lỗi nếu không có Activity
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF004578)), // Màu nền xanh Microsoft
            modifier = Modifier.fillMaxWidth().height(50.dp), // Chiều rộng đầy đủ và chiều cao 50dp
            shape = RoundedCornerShape(8.dp) // Bo góc nút
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) { // Sắp xếp nội dung của nút theo hàng
                Image(
                    painter = painterResource(id = R.drawable.logo_outlook), // Ảnh logo Outlook
                    contentDescription = "Outlook Logo", // Mô tả cho ảnh
                    modifier = Modifier
                        .size(24.dp) // Kích thước ảnh
                        .padding(end = 8.dp) // Khoảng cách giữa ảnh và văn bản
                )
                Text("Đăng nhập bằng Outlook", color = Color.White, fontSize = 16.sp) // Văn bản của nút
            }
        }
    }
}

// Hàm hiển thị Divider có văn bản ở giữa
@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // Căn giữa theo chiều dọc
        modifier = Modifier.fillMaxWidth() // Chiều rộng đầy đủ
    ) {
        Divider(
            color = Color.Gray, // Màu xám
            modifier = Modifier
                .weight(1f) // Chiếm 1 phần trọng số
                .height(1.dp) // Chiều cao 1dp
        )
        Text(
            text = text, // Văn bản được hiển thị
            modifier = Modifier.padding(horizontal = 8.dp) // Khoảng cách ngang
        )
        Divider(
            color = Color.Gray, // Màu xám
            modifier = Modifier
                .weight(1f) // Chiếm 1 phần trọng số
                .height(1.dp) // Chiều cao 1dp
        )
    }
}

// Hàm xem trước giao diện màn hình đăng nhập
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun LogInSreenPreview() {
    val navController = rememberNavController() // Tạo NavController giả để xem trước
    LogInScreen(navController) // Hiển thị màn hình đăng nhập
}