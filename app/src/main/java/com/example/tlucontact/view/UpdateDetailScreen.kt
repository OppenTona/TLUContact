package com.example.tlucontact.view
import com.example.tlucontact.viewmodel.StaffViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.R
import com.example.tlucontact.data.model.Staff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> UpdateDetailScreen(
    staff: Staff?,
    onBack: () -> Unit,
    onSave: (Staff) -> Unit,
    viewModel: StaffViewModel
) {
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf(staff?.name ?: "") }
    var phone by remember { mutableStateOf(staff?.phone ?: "") }
    var department by remember { mutableStateOf(staff?.department ?: "") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (staff != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ảnh đại diện và tên
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = rememberAsyncImagePainter(model = staff.avatarURL),
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { /* Xử lý chọn ảnh mới */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Chỉnh sửa ảnh")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Các trường thông tin có thể chỉnh sửa
                EditableField(label = "Mã giảng viên", value = staff.staffIdFB, editable = false)
                EditableField(label = "Chức vụ", value = staff.position, editable = false)
                EditableField(label = "Số điện thoại", value = phone, onValueChanged = { phone = it }, editable = true)
                EditableField(label = "Email", value = staff.staffId, editable = false)
                EditableField(label = "Đơn vị trực thuộc", value = department, onValueChanged = { department = it }, editable = true)

                Spacer(modifier = Modifier.height(32.dp))

                SaveCancelButtons(
                    onBack = onBack,
                    onSave = {
                        val updatedStaff = staff.copy(
                            name = name,
                            phone = phone,
                            department = department
                        )
                        viewModel.updateStaffInfo(updatedStaff)
                        onSave(updatedStaff)
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Cập nhật thông tin thành công")
                        }
                    }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
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
    var text by remember { mutableStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                if (editable) {
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

@Composable
fun SaveCancelButtons(onBack: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x80007AFF))
        ) {
            Text("Hủy", color = Color.White)
        }
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x80007AFF))
        ) {
            Text("Lưu", color = Color.White)
        }
    }
}