package com.example.tlucontact.view // Định nghĩa package của file Kotlin, tổ chức code theo cấu trúc thư mục

import android.widget.Toast // Import lớp Toast để hiển thị thông báo ngắn gọn trên giao diện
import androidx.compose.foundation.Image // Import Image từ thư viện Compose để hiển thị hình ảnh
import androidx.compose.foundation.clickable // Import clickable để thêm khả năng nhấn vào giao diện
import androidx.compose.foundation.layout.* // Import layout để tổ chức các phần tử giao diện
import androidx.compose.foundation.rememberScrollState // Import rememberScrollState để nhớ trạng thái cuộn
import androidx.compose.foundation.shape.RoundedCornerShape // Import RoundedCornerShape để tạo bo góc cho các phần tử
import androidx.compose.foundation.text.KeyboardOptions // Import KeyboardOptions để cấu hình bàn phím
import androidx.compose.foundation.verticalScroll // Import verticalScroll để cho phép cuộn theo chiều dọc
import androidx.compose.material.* // Import các thành phần giao diện cơ bản từ thư viện Material
import androidx.compose.material.icons.Icons // Import Icons để sử dụng các biểu tượng
import androidx.compose.material.icons.filled.Visibility // Import biểu tượng "Visibility" để hiển thị mật khẩu
import androidx.compose.material.icons.filled.VisibilityOff // Import biểu tượng "VisibilityOff" để ẩn mật khẩu
import androidx.compose.runtime.* // Import các API runtime cho Compose, ví dụ như mutableStateOf
import androidx.compose.ui.Alignment // Import Alignment để căn chỉnh các phần tử
import androidx.compose.ui.Modifier // Import Modifier để sửa đổi các phần tử giao diện
import androidx.compose.ui.graphics.Color // Import Color để sử dụng màu sắc
import androidx.compose.ui.platform.LocalConfiguration // Import LocalConfiguration để truy cập thông tin cấu hình thiết bị
import androidx.compose.ui.platform.LocalContext // Import LocalContext để truy cập ngữ cảnh ứng dụng
import androidx.compose.ui.res.painterResource // Import painterResource để sử dụng tài nguyên hình ảnh
import androidx.compose.ui.text.font.FontWeight // Import FontWeight để điều chỉnh độ đậm văn bản
import androidx.compose.ui.text.input.KeyboardType // Import KeyboardType để cấu hình kiểu nhập liệu
import androidx.compose.ui.text.input.PasswordVisualTransformation // Import PasswordVisualTransformation để che mật khẩu
import androidx.compose.ui.text.input.VisualTransformation // Import VisualTransformation để điều chỉnh cách hiển thị văn bản
import androidx.compose.ui.unit.dp // Import dp để sử dụng đơn vị đo lường
import androidx.compose.ui.unit.sp // Import sp để sử dụng đơn vị kích thước văn bản
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel để sử dụng ViewModel trong Compose
import androidx.navigation.NavController // Import NavController để điều khiển điều hướng
import com.example.tlucontact.R // Import R để truy cập tài nguyên ứng dụng
import com.example.tlucontact.viewmodel.SignupViewModel // Import SignupViewModel để quản lý dữ liệu cho màn hình đăng ký

// Hàm Composable để hiển thị màn hình đăng ký
@Composable
fun SignupScreen(navController: NavController, viewModel: SignupViewModel = viewModel()) {
    val configuration = LocalConfiguration.current // Lấy thông tin cấu hình thiết bị
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE // Kiểm tra thiết bị đang ở chế độ ngang hay dọc

    if (isLandscape) { // Nếu thiết bị đang ở chế độ ngang
        Row( // Sử dụng Row để bố trí các phần tử theo chiều ngang
            modifier = Modifier.fillMaxSize().padding(16.dp), // Sử dụng Modifier để chiếm toàn bộ kích thước và thêm khoảng cách
            verticalAlignment = Alignment.CenterVertically // Căn giữa các phần tử theo chiều dọc
        ) {
            Box( // Sử dụng Box để nhóm các phần tử
                modifier = Modifier.weight(1f), // Chia tỷ lệ kích thước ngang
                contentAlignment = Alignment.Center // Căn giữa nội dung
            ) {
                LogoSection() // Hiển thị phần logo
            }
            Spacer(modifier = Modifier.width(32.dp)) // Thêm khoảng trắng giữa các phần tử
            Box( // Một Box khác để chứa form đăng ký
                modifier = Modifier
                    .weight(1f) // Chia tỷ lệ kích thước ngang
                    .fillMaxHeight() // Chiếm toàn bộ chiều cao
                    .verticalScroll(rememberScrollState()) // Thêm khả năng cuộn dọc
            ) {
                SignupForm(navController, viewModel) // Hiển thị form đăng ký
            }
        }
    } else { // Nếu thiết bị đang ở chế độ dọc
        Column( // Sử dụng Column để bố trí các phần tử theo chiều dọc
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), // Thêm khả năng cuộn và khoảng cách
            horizontalAlignment = Alignment.CenterHorizontally // Căn giữa các phần tử theo chiều ngang
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Thêm khoảng trắng ở trên cùng
            LogoSection() // Hiển thị phần logo
            Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng trắng giữa logo và form
            SignupForm(navController, viewModel) // Hiển thị form đăng ký
        }
    }
}

// Hàm Composable để hiển thị phần logo và thông tin
@Composable
fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Sử dụng Column để bố trí phần tử theo chiều dọc và căn giữa
        Text("Đăng ký", fontSize = 24.sp, fontWeight = FontWeight.Bold) // Hiển thị tiêu đề "Đăng ký" với kích thước và độ đậm chữ
        Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng

        Image( // Hiển thị hình ảnh logo
            painter = painterResource(id = R.drawable.thuyloi), // Lấy tài nguyên hình ảnh từ R
            contentDescription = "Logo", // Nội dung mô tả cho hình ảnh
            modifier = Modifier.size(100.dp) // Đặt kích thước cho hình ảnh
        )

        Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng
        Text("Chỉ mất vài giây để kết nối với mọi người", fontSize = 14.sp, color = Color.Gray) // Hiển thị mô tả với màu xám
    }
}

// Hàm Composable để hiển thị form đăng ký
@Composable
fun SignupForm(navController: NavController, viewModel: SignupViewModel) {
    val context = LocalContext.current // Lấy ngữ cảnh hiện tại
    var passwordVisible by remember { mutableStateOf(false) } // Trạng thái hiển thị mật khẩu (ẩn/hiện)
    var confirmPasswordVisible by remember { mutableStateOf(false) } // Trạng thái hiển thị mật khẩu xác nhận

    // Lấy các giá trị từ ViewModel
    val name by viewModel.name.collectAsState() // Tên người dùng
    val phone by viewModel.phone.collectAsState() // Số điện thoại
    val email by viewModel.email.collectAsState() // Email người dùng
    val password by viewModel.password.collectAsState() // Mật khẩu
    val confirmPassword by viewModel.confirmPassword.collectAsState() // Mật khẩu xác nhận
    val isEmailInvalid by viewModel.isEmailInvalid.collectAsState() // Trạng thái email không hợp lệ
    val signupState by viewModel.signupState.collectAsState() // Trạng thái đăng ký
    val emailError by viewModel.emailError.collectAsState() // Lỗi email
    val passwordError by viewModel.passwordError.collectAsState() // Lỗi mật khẩu
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState() // Lỗi mật khẩu xác nhận
    val nameError by viewModel.nameError.collectAsState() // Lỗi tên
    val phoneError by viewModel.phoneError.collectAsState() // Lỗi số điện thoại

    LaunchedEffect(signupState) { // Lắng nghe sự thay đổi của signupState
        if (signupState.first) { // Nếu đăng ký thành công
            Toast.makeText(context, "Vui lòng kiểm tra email để xác minh", Toast.LENGTH_SHORT).show() // Hiển thị thông báo
            navController.navigate("login") // Chuyển sang màn hình đăng nhập
        } else if (signupState.second != null) { // Nếu có lỗi xảy ra
            Toast.makeText(context, "Lỗi: ${signupState.second}", Toast.LENGTH_SHORT).show() // Hiển thị thông báo lỗi
        }
    }

    Column { // Sử dụng Column để bố trí các phần tử theo chiều dọc
        // Trường nhập email
        TextField(
            value = email, // Giá trị email
            onValueChange = { viewModel.onEmailChange(it) }, // Hàm xử lý khi giá trị thay đổi
            label = { Text("Email", color = if (emailError) Color.Red else Color.Gray) }, // Gắn nhãn cho trường và thay đổi màu khi có lỗi
            colors = TextFieldDefaults.textFieldColors( // Tùy chỉnh màu sắc
                backgroundColor = Color.Transparent, // Nền trong suốt
                unfocusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu viền khi không được chọn
                focusedIndicatorColor = if (emailError) Color.Red else Color.Gray, // Màu viền khi được chọn
                focusedLabelColor = if (emailError) Color.Red else Color.Gray // Màu nhãn khi được chọn
            ),
            modifier = Modifier.fillMaxWidth() // Chiếm toàn bộ chiều rộng
        )

        Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng

        // Trường nhập mật khẩu
        PasswordField(
            value = password, // Giá trị mật khẩu
            onValueChange = { viewModel.onPasswordChange(it) }, // Hàm xử lý khi giá trị thay đổi
            label = "Mật khẩu", // Nhãn cho trường
            passwordVisible = passwordVisible, // Trạng thái hiển thị mật khẩu
            onVisibilityChange = { passwordVisible = it }, // Hàm xử lý khi thay đổi trạng thái hiển thị
            isError = passwordError // Trạng thái lỗi
        )

        Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng

        // Trường nhập lại mật khẩu
        PasswordField(
            value = confirmPassword, // Giá trị mật khẩu xác nhận
            onValueChange = { viewModel.onConfirmPasswordChange(it) }, // Hàm xử lý khi giá trị thay đổi
            label = "Nhập lại mật khẩu", // Nhãn cho trường
            passwordVisible = confirmPasswordVisible, // Trạng thái hiển thị mật khẩu
            onVisibilityChange = { confirmPasswordVisible = it }, // Hàm xử lý khi thay đổi trạng thái hiển thị
            isError = confirmPasswordError // Trạng thái lỗi
        )

        if (isEmailInvalid) { // Nếu email không hợp lệ

            Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng

            // Trường nhập họ và tên
            TextField(
                value = name, // Giá trị họ và tên
                onValueChange = { viewModel.onNameChange(it) }, // Hàm xử lý khi giá trị thay đổi
                label = { Text("Họ và tên", color = if (nameError) Color.Red else Color.Gray) }, // Gắn nhãn và thay đổi màu khi có lỗi
                colors = TextFieldDefaults.textFieldColors( // Tùy chỉnh màu sắc
                    backgroundColor = Color.Transparent, // Nền trong suốt
                    unfocusedIndicatorColor = if (nameError) Color.Red else Color.Gray, // Màu viền khi không được chọn
                    focusedIndicatorColor = if (nameError) Color.Red else Color.Gray, // Màu viền khi được chọn
                    focusedLabelColor = if (nameError) Color.Red else Color.Gray // Màu nhãn khi được chọn
                ),
                modifier = Modifier.fillMaxWidth() // Chiếm toàn bộ chiều rộng
            )

            Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng trắng

            // Trường nhập số điện thoại
            TextField(
                value = phone, // Giá trị số điện thoại
                onValueChange = { viewModel.onPhoneChange(it) }, // Hàm xử lý khi giá trị thay đổi
                label = { Text("Số điện thoại", color = if (phoneError) Color.Red else Color.Gray) }, // Gắn nhãn và thay đổi màu khi có lỗi
                colors = TextFieldDefaults.textFieldColors( // Tùy chỉnh màu sắc
                    backgroundColor = Color.Transparent, // Nền trong suốt
                    unfocusedIndicatorColor = if (phoneError) Color.Red else Color.Gray, // Màu viền khi không được chọn
                    focusedIndicatorColor = if (phoneError) Color.Red else Color.Gray, // Màu viền khi được chọn
                    focusedLabelColor = if (phoneError) Color.Red else Color.Gray // Màu nhãn khi được chọn
                ),
                modifier = Modifier.fillMaxWidth() // Chiếm toàn bộ chiều rộng
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng trắng

        // Nút đăng ký
        Button(
            onClick = {
                viewModel.signup() // Gọi hàm đăng ký từ ViewModel
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black), // Màu nền nút
            modifier = Modifier.fillMaxWidth().height(50.dp), // Kích thước nút
            shape = RoundedCornerShape(8.dp) // Bo góc nút
        ) {
            Text("Đăng ký", color = Color.White, fontSize = 16.sp) // Văn bản hiển thị trên nút
        }

        Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng trắng

        // Dòng văn bản chuyển đến màn hình đăng nhập
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { // Sử dụng Row để bố trí theo chiều ngang và căn giữa
            Text("Đã có tài khoản? ") // Văn bản thông báo
            Text( // Văn bản nhấn để chuyển màn hình
                "Đăng nhập",
                color = Color.Blue, // Màu xanh
                modifier = Modifier.clickable {
                    navController.navigate("login") // Điều hướng đến màn hình đăng nhập
                }
            )
        }
    }
}

// Hàm Composable để hiển thị trường mật khẩu
@Composable
fun PasswordField(
    value: String, // Giá trị của trường
    onValueChange: (String) -> Unit, // Hàm xử lý khi giá trị thay đổi
    label: String, // Nhãn của trường
    passwordVisible: Boolean, // Trạng thái hiển thị mật khẩu
    onVisibilityChange: (Boolean) -> Unit, // Hàm xử lý khi thay đổi trạng thái hiển thị
    isError: Boolean // Trạng thái lỗi
) {
    TextField(
        value = value, // Giá trị của trường
        onValueChange = onValueChange, // Hàm xử lý khi giá trị thay đổi
        label = { Text(label) }, // Gắn nhãn cho trường
        trailingIcon = { // Biểu tượng ở cuối trường
            IconButton(onClick = { onVisibilityChange(!passwordVisible) }) { // Nút để thay đổi trạng thái hiển thị mật khẩu
                Icon( // Hiển thị biểu tượng
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, // Biểu tượng tùy thuộc vào trạng thái
                    contentDescription = "Toggle Password" // Mô tả nội dung
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Che mật khẩu nếu cần
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Cấu hình bàn phím cho kiểu mật khẩu
        colors = TextFieldDefaults.textFieldColors( // Tùy chỉnh màu sắc
            backgroundColor = Color.Transparent, // Nền trong suốt
            unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray, // Màu viền khi không được chọn
            focusedIndicatorColor = if (isError) Color.Red else Color.Gray, // Màu viền khi được chọn
            focusedLabelColor = if (isError) Color.Red else Color.Gray // Màu nhãn khi được chọn
        ),
        modifier = Modifier.fillMaxWidth() // Chiếm toàn bộ chiều rộng
    )
}