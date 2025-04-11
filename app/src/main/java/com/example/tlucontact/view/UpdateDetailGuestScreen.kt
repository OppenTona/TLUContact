package com.example.tlucontact.view

import com.example.tlucontact.data.model.Guest
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
import com.example.tlucontact.viewmodel.GuestViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailGuestScreen(
    guest: Guest?,
    onBack: () -> Unit,
    viewModel: GuestViewModel,
) {
    val scrollState = rememberScrollState()
    var userId : String by remember { mutableStateOf(guest?.userId ?: "") }
    var name by remember { mutableStateOf(guest?.name ?: "") }
    var email by remember { mutableStateOf(guest?.email ?: "") }
    var phone by remember { mutableStateOf(guest?.phone ?: "") }
    var avatarURL by remember { mutableStateOf(guest?.avatarURL ?: "") }
    var position by remember { mutableStateOf(guest?.position ?: "") }
    var department by remember { mutableStateOf(guest?.department ?: "") }
    var address by remember { mutableStateOf(guest?.address ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    // Lấy dữ liệu Guest hiện tại bằng email
    LaunchedEffect(Unit) {
        val currentEmail = FirebaseAuth.getInstance().currentUser?.email
        currentEmail?.let { email ->
            viewModel.fetchGuestByEmail(email)
        }

        viewModel.snackbarMessage.collectLatest { message ->
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSnackbar()
            }
        }
    }

    // Cập nhật state khi guest được load
    LaunchedEffect(Unit) {
        viewModel.selectedGuest.collectLatest { guest ->
            guest?.let {
                userId = it.userId
                name = it.name
                email = it.email
                phone = it.phone
                avatarURL = it.avatarURL
                position = it.position
                department = it.department
                address = it.address
            }
        }
    }

    val onSaveClicked = {
        val updatedGuest = Guest(
            email = email,
            phone = phone,
            name = name,
            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            avatarURL = avatarURL,
            department = department,
            position = position,
            address = address,
            userType = "guest",
            userId = userId
        )
        viewModel.updateGuestInfo(updatedGuest)
    }

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
        if (guest != null) {
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
                    Log.d("UpdateDetailScreen", "Image URL: ${guest.avatarURL}")
                    Image(
                        painter = rememberAsyncImagePainter(model = guest.avatarURL),
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
                Text(text = guest.name, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Editable fields

                GuestEditableField(label = "Mã sinh viên/ cán bộ", value = guest.userId, onValueChange = {}, editable = false)
                GuestEditableField(label = "Email", value = guest.email, onValueChange = {}, editable = false)
                GuestEditableField(label = "Số điện thoại", value = phone, onValueChange = { phone = it }, editable = true)
                GuestEditableField(label = "Chức vụ", value = guest.position, onValueChange = {}, editable = false)
                GuestEditableField(label = "Phòng ban", value = guest.department, onValueChange = {}, editable = false)
                GuestEditableField(label = "Địa chỉ", value = address, onValueChange = { address = it }, editable = true)


                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Hủy", color = Color.White)
                    }
                    Button(onClick = {
                        val updatedGuest = guest.copy(
                            userId = userId,
                            name = name,
                            email = email,
                            phone = phone,
                            avatarURL = avatarURL,
                            position = position,
                            department = department,
                            address = address
                        )
                        viewModel.updateGuestInfo(updatedGuest)
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
fun GuestEditableField(
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