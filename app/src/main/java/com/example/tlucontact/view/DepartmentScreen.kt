package com.example.tlucontact.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tlucontact.Department
import com.example.tlucontact.viewmodel.DepartmentViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DepartmentScreen(navController: NavController, viewModel: DepartmentViewModel = viewModel()) {
    val departments by viewModel.departments.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Danh sách đơn vị", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        LazyColumn {
            items(departments) { department ->
                DepartmentItem(department, navController)
            }
        }
    }
}

@Composable
fun DepartmentItem(department: Department, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("department_detail/${department.id}")
            },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = department.name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            // Hiển thị thông tin chi tiết của đơn vị
        // Text(text = department.departmentDescription)
        }
    }
}

