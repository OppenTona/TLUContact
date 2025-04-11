//package com.example.tlucontact.view
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.example.tlucontact.data.model.Department
//import com.example.tlucontact.viewmodel.DepartmentViewModel
//
//@Composable
//fun DepartmentListScreen(viewModel: DepartmentViewModel, navController: NavController) {
//    val departments by viewModel.departmentList.collectAsState()
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Danh bạ đơn vị",
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(departments) { department ->
//                DepartmentItem(department = department, navController = navController)
//            }
//        }
//    }
//}
//
////@Composable
////fun DepartmentItem(department: Department, navController: NavController) {
////    Row(
////        modifier = Modifier
////            .fillMaxWidth()
////            .clickable {
////                // Điều hướng đến màn hình chi tiết của đơn vị
////                navController.navigate("department_detail/${department.nameDepartment}/${department.departmentID}/${department.leader}/${department.email}/${department.phone}/${department.address}")
////            }
////            .padding(8.dp),
////        verticalAlignment = Alignment.CenterVertically
////    ) {
////        Text(
////            text = department.nameDepartment, // Chỉ hiển thị tên đơn vị
////            fontWeight = FontWeight.Bold,
////            modifier = Modifier.padding(start = 8.dp)
////        )
////    }
////}
