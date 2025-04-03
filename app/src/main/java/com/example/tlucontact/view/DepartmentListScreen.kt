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
import com.example.tlucontact.Department
import com.example.tlucontact.departmentList
import com.example.tlucontact.viewmodel.DepartmentViewModel

@Composable
fun DepartmentListScreen(viewModel: DepartmentViewModel, navController: NavController) {
    val departments by viewModel.departments.collectAsState()

    Column {
        Text(
            text = "Danh bạ đơn vị",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )


        LazyColumn {
            items(departments) { department ->
                DepartmentItem(department = department, navController = navController)
            }
        }
    }
}

@Composable
fun DepartmentItem(department: com.example.tlucontact.data.model.Department, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Điều hướng đến màn hình chi tiết sinh viên với các tham số
                val encodedName = java.net.URLEncoder.encode(department.nameDepartment, "UTF-8")
                val encodedId = java.net.URLEncoder.encode(department.departmentID, "UTF-8")
                val encodedEmail = java.net.URLEncoder.encode(department.email, "UTF-8")
                val encodedLeader = java.net.URLEncoder.encode(department.leader, "UTF-8")
                val encodedPhone = java.net.URLEncoder.encode(department.phone, "UTF-8")
                val encodedAddress = java.net.URLEncoder.encode(department.address, "UTF-8")

                navController.navigate("department_detail/$encodedName/$encodedId/$encodedLeader/$encodedEmail/$encodedPhone/$encodedAddress")
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = department.photoURL,
            contentDescription = "Avatar",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = department.nameDepartment, fontWeight = FontWeight.Bold)
            Text(text = department.email)
        }
    }
}
