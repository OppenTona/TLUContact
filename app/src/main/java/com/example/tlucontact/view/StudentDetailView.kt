package com.example.tlucontact.view


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.viewmodel.StudentDetailViewModel

@Composable
fun StudentDetailScreen(viewModel: StudentDetailViewModel, studentId: String) {
    LaunchedEffect(studentId) {
        viewModel.fetchStudent(studentId)
    }

    val student by viewModel.student.collectAsState()

    student?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(model = it.photoURL, contentDescription = "Avatar", modifier = Modifier.size(80.dp))
            TextField(value = it.fullNameStudent, onValueChange = {}, label = { Text("Họ và tên") })
            TextField(value = it.email, onValueChange = {}, label = { Text("Email") })
            Button(onClick = { viewModel.updateStudent(it) }) {
                Text("Lưu")
            }
        }
    }
}
