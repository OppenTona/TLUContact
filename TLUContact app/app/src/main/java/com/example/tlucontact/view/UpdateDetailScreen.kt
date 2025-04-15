package com.example.tlucontact.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Staff
import com.example.tlucontact.viewmodel.StaffViewModel


@OptIn(ExperimentalMaterial3Api::class) // Cho phép dùng API đánh dấu là experimental trong Material3
@Composable
fun UpdateDetailScreen( // Hàm composable cho màn hình chỉnh sửa thông tin
    staff: Staff?, // Tham số truyền vào là đối tượng Staff cần chỉnh sửa
    onBack: () -> Unit, // Hàm callback khi bấm nút quay lại
    onSave: (Staff) -> Unit // Hàm callback khi bấm nút lưu
) {
    val staffViewModel: StaffViewModel = viewModel() // Lấy ViewModel để xử lý logic
    val scrollState = rememberScrollState() // Ghi nhớ trạng thái cuộn của màn hình
    var name by remember { mutableStateOf(staff?.name ?: "") } // Ghi nhớ giá trị tên từ staff
    var phone by remember { mutableStateOf(staff?.phone ?: "") } // Ghi nhớ số điện thoại
    var department by remember { mutableStateOf(staff?.department ?: "") } // Ghi nhớ đơn vị
    var position by remember { mutableStateOf(staff?.position ?: "") } // Ghi nhớ chức vụ
    var staffidfb by remember { mutableStateOf(staff?.staffIdFB ?: "") } // Ghi nhớ mã giảng viên từ Firestore

    val snackbarHostState = remember { SnackbarHostState() } // Ghi nhớ trạng thái snackbar
    val updateMessage by staffViewModel.updateMessage.collectAsState() // Lấy thông báo cập nhật từ ViewModel
    val context = LocalContext.current // Lấy context hiện tại để hiển thị Toast

    val imageUri = remember { mutableStateOf<Uri?>(null) } // Biến để lưu Uri ảnh người dùng chọn

    val imagePickerLauncher = rememberLauncherForActivityResult( // Tạo launcher để chọn ảnh từ thiết bị
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri // Khi chọn xong ảnh thì lưu lại Uri vào biến imageUri
    }

    Scaffold( // UI cơ bản gồm top bar và nội dung
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar( // Thanh tiêu đề trên cùng
                title = { Text("Chỉnh sửa thông tin") },
                navigationIcon = {
                    IconButton(onClick = onBack) { // Nút quay lại
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (staff != null) { // Nếu có dữ liệu staff
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) { // Hiển thị ảnh đại diện và nút chỉnh sửa ảnh
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri.value ?: staff.avatarURL),
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*") // Mở trình chọn ảnh
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Chỉnh sửa ảnh")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                EditableField(label = "Họ và tên", value = name, editable = true, onValueChanged = { name = it }) // Trường chỉnh tên
                EditableField(label = "Mã giảng viên", value = staff.staffIdFB, editable = true, onValueChanged = { staffidfb = it }) // Trường mã GV
                EditableField(label = "Chức vụ", value = position, editable = true, onValueChanged = { position = it }) // Trường chức vụ
                EditableField(label = "Số điện thoại", value = phone, editable = true, onValueChanged = { phone = it }) // Trường SDT
                EditableField(label = "Email", value = staff.staffId, editable = false) // Trường email không chỉnh được
                EditableField(label = "Đơn vị trực thuộc", value = department, editable = true, onValueChanged = { department = it }) // Trường đơn vị

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))
                    ) {
                        Text("Hủy", color = Color.White)
                    }
                    Button(
                        onClick = {
                            if (imageUri.value != null) { // Nếu người dùng chọn ảnh mới
                                staffViewModel.uploadImageToStorage(
                                    uri = imageUri.value!!,
                                    onSuccess = { imageUrl ->
                                        val updatedStaff = staff.copy( // Tạo staff mới với ảnh mới
                                            name = name,
                                            phone = phone,
                                            staffIdFB = staffidfb,
                                            department = department,
                                            position = position,
                                            avatarURL = imageUrl
                                        )
                                        staffViewModel.updateStaffInfo(updatedStaff) // Gửi lên ViewModel
                                        onSave(updatedStaff)
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else { // Nếu không đổi ảnh
                                val updatedStaff = staff.copy(
                                    name = name,
                                    phone = phone,
                                    staffIdFB = staffidfb,
                                    department = department,
                                    position = position,
                                    avatarURL = staff.avatarURL
                                )
                                staffViewModel.updateStaffInfo(updatedStaff)
                                onSave(updatedStaff)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Hiển thị vòng tròn load nếu chưa có dữ liệu staff
            }
        }
    }

    LaunchedEffect(updateMessage) { // Theo dõi updateMessage để hiển thị Toast nếu có
        updateMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            staffViewModel.clearUpdateMessage()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableField(
    label: String,
    value: String,
    editable: Boolean,
    onValueChanged: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf(value) } // Ghi nhớ giá trị nhập vào

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                if (editable) { // Nếu được phép chỉnh sửa thì cho thay đổi
                    text = it
                    onValueChanged(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !editable,
            shape = RoundedCornerShape(16.dp),
            label = { Text(label, color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                focusedIndicatorColor = Color(0xFF007AFF),
                unfocusedIndicatorColor = Color(0xFFE0E0E0),
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}


