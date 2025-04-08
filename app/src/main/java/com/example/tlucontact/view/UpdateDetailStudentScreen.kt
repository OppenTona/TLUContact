package com.example.tlucontact.view

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tlucontact.data.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDetailStudentScreen(
    student: Student?,
    onBack: () -> Unit,
    onSave: (Student) -> Unit
) {
    val scrollState = rememberScrollState()
    var fullName by remember { mutableStateOf(student?.fullNameStudent ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    var className by remember { mutableStateOf(student?.className ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Student Details") },
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
                StudentEditableField(label = "Full Name", value = student.fullNameStudent, editable = true)
                StudentEditableField(label = "Phone", value = student.phone, editable = true)
                StudentEditableField(label = "Address", value = student.address, editable = true)
                StudentEditableField(label = "Class", value = student.className, editable = false)

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Cancel", color = Color.White)
                    }
                    Button(onClick = {
                        // Save updated student details
                        onSave(student.copy(fullNameStudent = fullName, phone = phone, address = address))
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B7FF))) {
                        Text("Save", color = Color.White)
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
fun StudentEditableField(label: String, value: String, editable: Boolean) {
    var text by remember { mutableStateOf(value) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { if (editable) text = it },
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