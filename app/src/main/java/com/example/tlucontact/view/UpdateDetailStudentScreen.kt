package com.example.tlucontact.view

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.viewmodel.StudentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.Activity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tlucontact.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailStudentScreen(
    student: Student?,
    onBack: () -> Unit,
    onSave: (Student) -> Unit,
    viewModel: StudentViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var fullName by remember { mutableStateOf(student?.fullNameStudent ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (student != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile picture and name
                Box(contentAlignment = Alignment.BottomEnd) {
                    Log.d("UpdateDetailScreen", "Image URL: ${student.photoURL}")
                    Image(
                        painter = rememberAsyncImagePainter(model = student.photoURL),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { /* Handle image selection */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Picture")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = student.fullNameStudent, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Editable fields
                StudentEditableField(label = "Mã sinh viên", value = student.studentID, onValueChange = {}, editable = false)
                StudentEditableField(label = "Lớp", value = student.className, onValueChange = {}, editable = false)
                StudentEditableField(label = "Số điện thoại", value = phone, onValueChange = { phone = it }, editable = true)
                StudentEditableField(label = "Email", value = student.email, onValueChange = {}, editable = false)
                StudentEditableField(label = "Địa chỉ nơi ở", value = address, onValueChange = { address = it }, editable = true)

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Hủy", color = Color.White)
                    }
                    Button(onClick = {
                        val updatedStudent = student.copy(
                            fullNameStudent = fullName,
                            phone = phone,
                            address = address
                        )
                        viewModel.updateStudentInfo(updatedStudent)
                        // Hiển thị thông báo
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Cập nhật thông tin thành công")
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Lưu", color = Color.White)
                    }

                }
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
fun StudentEditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    editable: Boolean
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (editable) onValueChange(it) },
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
