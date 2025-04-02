package com.example.tlucontact.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.viewmodel.StudentViewModel

@Composable
fun StudentListScreen(viewModel: StudentViewModel, navController: NavController) {
    val students by viewModel.students.collectAsState()

    Column {
        Text(
            text = "Danh bạ sinh viên",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(students) { student ->
                StudentItem(student = student, navController = navController)
            }
        }
    }
}

@Composable
fun StudentItem(student: Student, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Điều hướng đến màn hình chi tiết sinh viên với các tham số
                val encodedName = java.net.URLEncoder.encode(student.fullNameStudent, "UTF-8")
                val encodedId = java.net.URLEncoder.encode(student.studentID, "UTF-8")
                val encodedClassName = java.net.URLEncoder.encode(student.className, "UTF-8")
                val encodedEmail = java.net.URLEncoder.encode(student.email, "UTF-8")
                val encodedPhone = java.net.URLEncoder.encode(student.phone, "UTF-8")
                val encodedAddress = java.net.URLEncoder.encode(student.address, "UTF-8")

                navController.navigate("student_detail/$encodedName/$encodedId/$encodedClassName/$encodedEmail/$encodedPhone/$encodedAddress")
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = student.photoURL,
            contentDescription = "Avatar",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = student.fullNameStudent, fontWeight = FontWeight.Bold)
            Text(text = student.email)
        }
    }
}
