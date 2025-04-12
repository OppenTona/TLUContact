package com.example.tlucontact.view // Định nghĩa package chứa file này

import com.example.tlucontact.data.model.Guest // Import lớp Guest từ package data.model
import android.util.Log // Import lớp Log để ghi log
import androidx.compose.foundation.Image // Import thành phần Image từ Compose
import androidx.compose.foundation.layout.* // Import các thành phần bố cục từ Compose
import androidx.compose.foundation.rememberScrollState // Import trạng thái cuộn
import androidx.compose.foundation.shape.CircleShape // Import hình dạng hình tròn
import androidx.compose.foundation.shape.RoundedCornerShape // Import hình dạng góc bo tròn
import androidx.compose.foundation.verticalScroll // Import cuộn dọc
import androidx.compose.material3.* // Import các thành phần Material 3
import androidx.compose.material.icons.Icons // Import biểu tượng Icons
import androidx.compose.material.icons.filled.ArrowBack // Import biểu tượng mũi tên quay lại
import androidx.compose.material.icons.filled.Edit // Import biểu tượng chỉnh sửa
import androidx.compose.runtime.* // Import các thành phần runtime Compose
import androidx.compose.ui.Alignment // Import căn chỉnh từ Compose UI
import androidx.compose.ui.Modifier // Import Modifier để chỉnh sửa giao diện
import androidx.compose.ui.draw.clip // Import clip để cắt hình
import androidx.compose.ui.graphics.Color // Import lớp màu sắc
import androidx.compose.ui.layout.ContentScale // Import tỷ lệ nội dung
import androidx.compose.ui.platform.LocalContext // Import context hiện tại
import androidx.compose.ui.unit.dp // Import đơn vị đo dp
import androidx.compose.ui.unit.sp // Import đơn vị đo sp
import coil.compose.rememberAsyncImagePainter // Import bộ nhớ đệm ảnh từ Coil
import kotlinx.coroutines.CoroutineScope // Import phạm vi Coroutine
import kotlinx.coroutines.Dispatchers // Import bộ điều phối Coroutine
import kotlinx.coroutines.launch // Import hàm launch cho Coroutine
import android.app.Activity // Import lớp Activity
import com.example.tlucontact.viewmodel.GuestViewModel // Import ViewModel của Guest
import com.google.firebase.auth.FirebaseAuth // Import Firebase Authentication
import kotlinx.coroutines.flow.collectLatest // Import hàm collectLatest từ Flow

// Hàm chú thích màn hình cập nhật thông tin chi tiết khách
@OptIn(ExperimentalMaterial3Api::class) // Sử dụng API Material 3 đang thử nghiệm
@Composable // Đánh dấu hàm là composable (giao diện)
fun UpdateDetailGuestScreen(
    guest: Guest?, // Tham số khách hiện tại
    onBack: () -> Unit, // Hàm được gọi khi nhấn nút quay lại
    viewModel: GuestViewModel, // ViewModel quản lý dữ liệu cho giao diện này
) {
    val scrollState = rememberScrollState() // Tạo trạng thái cuộn
    var userId : String by remember { mutableStateOf(guest?.userId ?: "") } // Tạo state cho userId
    var name by remember { mutableStateOf(guest?.name ?: "") } // Tạo state cho tên
    var email by remember { mutableStateOf(guest?.email ?: "") } // Tạo state cho email
    var phone by remember { mutableStateOf(guest?.phone ?: "") } // Tạo state cho số điện thoại
    var avatarURL by remember { mutableStateOf(guest?.avatarURL ?: "") } // Tạo state cho URL ảnh đại diện
    var position by remember { mutableStateOf(guest?.position ?: "") } // Tạo state cho chức vụ
    var department by remember { mutableStateOf(guest?.department ?: "") } // Tạo state cho phòng ban
    var address by remember { mutableStateOf(guest?.address ?: "") } // Tạo state cho địa chỉ
    val snackbarHostState = remember { SnackbarHostState() } // Tạo trạng thái cho Snackbar

    // Lấy dữ liệu khách hiện tại bằng email
    LaunchedEffect(Unit) { // Hiệu ứng được chạy khi giao diện được tạo
        val currentEmail = FirebaseAuth.getInstance().currentUser?.email // Lấy email người dùng hiện tại từ Firebase
        currentEmail?.let { email -> // Nếu có email, thực hiện:
            viewModel.fetchGuestByEmail(email) // Gọi ViewModel để lấy khách theo email
        }

        viewModel.snackbarMessage.collectLatest { message -> // Lắng nghe thông báo Snackbar từ ViewModel
            message?.let {
                snackbarHostState.showSnackbar(it) // Hiển thị thông báo Snackbar
                viewModel.clearSnackbar() // Xóa thông báo sau khi hiển thị
            }
        }
    }

    // Cập nhật state khi khách được load
    LaunchedEffect(Unit) { // Hiệu ứng được chạy khi giao diện được tạo
        viewModel.selectedGuest.collectLatest { guest -> // Lắng nghe khách được chọn từ ViewModel
            guest?.let {
                userId = it.userId // Cập nhật userId
                name = it.name // Cập nhật tên
                email = it.email // Cập nhật email
                phone = it.phone // Cập nhật số điện thoại
                avatarURL = it.avatarURL // Cập nhật URL ảnh đại diện
                position = it.position // Cập nhật chức vụ
                department = it.department // Cập nhật phòng ban
                address = it.address // Cập nhật địa chỉ
            }
        }
    }

    val onSaveClicked = { // Hàm xử lý khi nhấn nút lưu
        val updatedGuest = Guest( // Tạo đối tượng khách được cập nhật
            email = email,
            phone = phone,
            name = name,
            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "", // Lấy userId từ Firebase
            avatarURL = avatarURL,
            department = department,
            position = position,
            address = address,
            userType = "guest", // Loại người dùng là "guest"
            userId = userId
        )
        viewModel.updateGuestInfo(updatedGuest) // Gọi ViewModel để cập nhật thông tin khách
    }

    Scaffold( // Thành phần giao diện chính
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Host cho Snackbar
        topBar = { // Thanh công cụ trên cùng
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin") }, // Tiêu đề
                navigationIcon = { // Nút quay lại
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back") // Biểu tượng mũi tên quay lại
                    }
                }
            )
        }
    ) { paddingValues -> // Nội dung giao diện
        if (guest != null) { // Nếu khách không null
            Column( // Tạo bố cục cột
                modifier = Modifier
                    .fillMaxSize() // Chiếm toàn bộ kích thước
                    .verticalScroll(scrollState) // Cho phép cuộn dọc
                    .padding(paddingValues) // Thêm khoảng cách padding
                    .padding(16.dp), // Padding 16dp
                horizontalAlignment = Alignment.CenterHorizontally // Căn giữa nội dung theo chiều ngang
            ) {
                // Ảnh đại diện và tên
                Box(contentAlignment = Alignment.BottomEnd) { // Hộp căn chỉnh nội dung
                    Log.d("UpdateDetailScreen", "Image URL: ${guest.avatarURL}") // Ghi log URL ảnh
                    Image(
                        painter = rememberAsyncImagePainter(model = guest.avatarURL), // Tải ảnh từ URL
                        contentDescription = "Profile Picture", // Mô tả ảnh
                        modifier = Modifier
                            .size(100.dp) // Kích thước 100dp
                            .clip(CircleShape), // Cắt ảnh thành hình tròn
                        contentScale = ContentScale.Crop // Tỷ lệ ảnh là crop
                    )
                    IconButton(
                        onClick = { /* Handle image selection */ }, // Xử lý khi nhấn nút chọn ảnh
                        modifier = Modifier.size(24.dp) // Kích thước nút 24dp
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Picture") // Biểu tượng chỉnh sửa ảnh
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Thêm khoảng cách 8dp
                Text(text = guest.name, fontSize = 20.sp) // Hiển thị tên khách với font size 20sp
                Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách 16dp

                // Các trường thông tin chỉnh sửa
                GuestEditableField(label = "Mã sinh viên/ cán bộ", value = guest.userId, onValueChange = {}, editable = false) // Trường mã không chỉnh sửa
                GuestEditableField(label = "Email", value = guest.email, onValueChange = {}, editable = false) // Trường email không chỉnh sửa
                GuestEditableField(label = "Số điện thoại", value = phone, onValueChange = { phone = it }, editable = true) // Trường số điện thoại có thể chỉnh sửa
                GuestEditableField(label = "Chức vụ", value = guest.position, onValueChange = {}, editable = false) // Trường chức vụ không chỉnh sửa
                GuestEditableField(label = "Phòng ban", value = guest.department, onValueChange = {}, editable = false) // Trường phòng ban không chỉnh sửa
                GuestEditableField(label = "Địa chỉ", value = address, onValueChange = { address = it }, editable = true) // Trường địa chỉ có thể chỉnh sửa

                Spacer(modifier = Modifier.height(32.dp)) // Thêm khoảng cách 32dp

                // Hàng chứa các nút
                Row(
                    modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
                    horizontalArrangement = Arrangement.SpaceEvenly // Căn đều các nút
                ) {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) { // Nút hủy
                        Text("Hủy", color = Color.White) // Nội dung nút là "Hủy"
                    }
                    Button(onClick = { // Nút lưu
                        val updatedGuest = guest.copy( // Tạo bản sao khách với thông tin đã chỉnh sửa
                            userId = userId,
                            name = name,
                            email = email,
                            phone = phone,
                            avatarURL = avatarURL,
                            position = position,
                            department = department,
                            address = address
                        )
                        viewModel.updateGuestInfo(updatedGuest) // Gọi ViewModel để cập nhật thông tin
                        // Hiển thị thông báo
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Cập nhật thông tin thành công") // Thông báo cập nhật thành công
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Lưu", color = Color.White) // Nội dung nút là "Lưu"
                    }
                }
            }
        } else { // Nếu khách null
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { // Hộp căn giữa nội dung
                CircularProgressIndicator() // Hiển thị vòng tròn tải
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Sử dụng API Material 3 đang thử nghiệm
@Composable // Đánh dấu hàm là composable
fun GuestEditableField(
    label: String, // Nhãn của trường
    value: String, // Giá trị hiện tại của trường
    onValueChange: (String) -> Unit, // Hàm xử lý khi giá trị thay đổi
    editable: Boolean // Trường có thể chỉnh sửa hay không
) {
    Column(modifier = Modifier
        .fillMaxWidth() // Chiếm toàn bộ chiều rộng
        .padding(vertical = 4.dp)) { // Thêm padding 4dp theo chiều dọc
        OutlinedTextField( // Trường nhập liệu có viền
            value = value, // Giá trị hiện tại
            onValueChange = { if (editable) onValueChange(it) }, // Xử lý thay đổi giá trị nếu có thể chỉnh sửa
            modifier = Modifier.fillMaxWidth(), // Chiếm toàn bộ chiều rộng
            readOnly = !editable, // Nếu không thể chỉnh sửa thì đặt là chỉ đọc
            shape = RoundedCornerShape(16.dp), // Góc bo tròn 16dp
            label = { Text(label, color = Color.Gray) }, // Nhãn của trường
            colors = TextFieldDefaults.colors( // Thiết lập màu sắc
                focusedTextColor = Color.Black, // Màu chữ khi được chọn
                unfocusedTextColor = Color.Black, // Màu chữ khi không được chọn
                disabledTextColor = Color.Black, // Màu chữ khi bị vô hiệu hóa
                focusedContainerColor = Color.White, // Màu nền khi được chọn
                unfocusedContainerColor = Color.White, // Màu nền khi không được chọn
                disabledContainerColor = Color(0xFFE0E0E0), // Màu nền khi bị vô hiệu hóa
                focusedIndicatorColor = Color(0xFF007AFF), // Màu viền khi được chọn
                unfocusedIndicatorColor = Color(0xFFE0E0E0), // Màu viền khi không được chọn
                disabledIndicatorColor = Color.Transparent // Màu viền khi bị vô hiệu hóa
            )
        )
    }
}