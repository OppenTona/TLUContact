package com.example.tlucontact.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.viewmodel.DepartmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailDepartmentScreen(
    department: Department?,
    onBack: () -> Unit,
    onSave: (Department) -> Unit,
    viewModel: DepartmentViewModel
) {
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(department?.name ?: "") }
    var leader by remember { mutableStateOf(department?.leader ?: "") }
    var email by remember { mutableStateOf(department?.email ?: "") }
    var phone by remember { mutableStateOf(department?.phone ?: "") }
    var address by remember { mutableStateOf(department?.address ?: "") }
    var type by remember { mutableStateOf(department?.type ?: "") }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin đơn vị") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (department != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                DepartmentEditableField(label = "Tên đơn vị", value = name, onValueChange = { name = it })
                DepartmentEditableField(label = "Trưởng đơn vị", value = leader, onValueChange = { leader = it })
                DepartmentEditableField(label = "Email", value = email, onValueChange = { email = it })
                DepartmentEditableField(label = "Số điện thoại", value = phone, onValueChange = { phone = it })
                DepartmentEditableField(label = "Địa chỉ", value = address, onValueChange = { address = it })
                DepartmentEditableField(label = "Loại đơn vị", value = type, onValueChange = { type = it })

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Hủy", color = Color.White)
                    }
                    Button(onClick = {
                        val updatedDepartment = department.copy(
                            name = name,
                            leader = leader,
                            email = email,
                            phone = phone,
                            address = address,
                            type = type
                        )
                        viewModel.updateDepartmentInfo(updatedDepartment)
                        onSave(updatedDepartment)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Không tìm thấy thông tin đơn vị")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentEditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
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