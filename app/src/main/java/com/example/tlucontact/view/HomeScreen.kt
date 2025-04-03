package com.example.tlucontact.view

import android.content.Intent
import com.example.tlucontact.DetailScreen
import com.example.tlucontact.MainActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.tlucontact.R
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.model.Staff
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.data.repository.DepartmentRepository
import com.example.tlucontact.data.repository.SessionManager
import com.example.tlucontact.viewmodel.DepartmentViewModel
import com.example.tlucontact.viewmodel.DepartmentViewModelFactory
import com.example.tlucontact.viewmodel.StaffViewModel
import com.example.tlucontact.viewmodel.StudentViewModel

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "directory"
            ) {
                composable(route = "update_detail") {
                    UpdateDetailScreen(
                        onBack = { navController.popBackStack() },
                        onSave = { /* Xử lý lưu thông tin */ }
                    )
                }

                composable("directory") {
                    Directoryscreen(navController = navController)
                }

                composable(
                    route = "student_detail/{name}/{studentId}/{className}/{email}/{phone}/{address}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("studentId") { type = NavType.StringType },
                        navArgument("className") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("address") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val args = backStackEntry.arguments!!
                    DetailScreen(
                        navController = navController,
                        screenTitle = args.getString("screenTitle") ?: "sinh viên",
                        name = args.getString("name") ?: "",
                        studentId = args.getString("studentId") ?: "",
                        className = args.getString("className") ?: "",
                        email = args.getString("email") ?: "",
                        phone = args.getString("phone") ?: "",
                        address = args.getString("address") ?: ""
                    )
                }

                composable(route = "DetailContactScreen") {
                    val staff =
                        navController.previousBackStackEntry?.savedStateHandle?.get<Staff>("staff")
                            ?: Staff("", "", "", "")

                    DetailContactScreen(
                        staff = staff,
                        onBack = { navController.popBackStack() },
                    )
                }

                composable(
                    route = "department_detail/{name}/{code}/{leader}/{email}/{phone}/{address}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("code") { type = NavType.StringType },
                        navArgument("leader") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("address") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val args = backStackEntry.arguments!!
                    DetailScreen(
                        navController = navController,
                        screenTitle = args.getString("screenTitle") ?: "đơn vị",
                        name = args.getString("name") ?: "",
                        studentId = args.getString("code") ?: "",
                        className = args.getString("leader") ?: "",
                        email = args.getString("email") ?: "",
                        phone = args.getString("phone") ?: "",
                        address = args.getString("address") ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun Directoryscreen(
    navController: NavController,
    staffViewModel: StaffViewModel = StaffViewModel(),
    studentViewModel: StudentViewModel = StudentViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Giảng viên") }
    var query by remember { mutableStateOf("") }
    val staffs by staffViewModel.staffList.collectAsState()
    val students by studentViewModel.studentList.collectAsState()

    val departmentRepository = DepartmentRepository()
    val departmentViewModel: DepartmentViewModel = viewModel(
        factory = DepartmentViewModelFactory(departmentRepository)
    )
    val departments by departmentViewModel.departmentList.collectAsState()

    Scaffold(
        bottomBar = {
            Bottomnavigationbar(selectedTab) { newTab ->
                selectedTab = newTab
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Topbar(
                title = "Danh bạ $selectedTab",
                onLogoutClick = {
                    val sessionManager = SessionManager(context)
                    sessionManager.clearSession()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(16.dp))
            Searchbar(query = query, onQueryChange = { query = it })
            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Useravatar(navController)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Hồ sơ của bạn", fontSize = 14.sp, color = Color.Gray)
                    Text("Nguyễn Thị Mai Hương", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == "Giảng viên") {
                Stafflist(staffs = staffs, query = query, navController = navController)
            } else if (selectedTab == "Đơn vị") {
                DepartmentList(departments = departments, query = query, navController = navController)
            } else if (selectedTab == "Sinh viên") {
                StudentList(students = students, query = query, navController = navController)
            }
        }
    }
}

@Composable
fun StudentList(students: List<Student>, query: String, navController: NavController) {
    var sortAscending by remember { mutableStateOf(true) }

    val filteredStudents = students.filter { it.fullNameStudent.contains(query, ignoreCase = true) }
    val sortedStudents = if (sortAscending) {
        filteredStudents.sortedBy { it.fullNameStudent.lowercase() }
    } else {
        filteredStudents.sortedByDescending { it.fullNameStudent.lowercase() }
    }

    val groupedStudents = ('A'..'Z').associateWith { letter ->
        sortedStudents.filter { it.fullNameStudent.firstOrNull()?.uppercaseChar() == letter }
    }

    LazyColumn {
        groupedStudents.forEach { (letter, studentList) ->
            item {
                Text(
                    text = letter.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                )
            }

            items(studentList) { student ->
                StudentItem(
                    student = student,
                    isSelected = false,
                    onClick = {
                        navController.navigate("student_detail/${student.fullNameStudent}/${student.studentID}/${student.className}/${student.email}/${student.phone}/${student.address}")
                    },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun StudentItem(
    student: Student,
    isSelected: Boolean,
    onClick: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = student.photoURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = student.fullNameStudent, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = student.className, fontSize = 14.sp, color = Color.Gray)
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: ${student.email}", fontSize = 14.sp)
            Text("Số điện thoại: ${student.phone}", fontSize = 14.sp)
            Text("Địa chỉ: ${student.address}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}
@Composable
fun DepartmentList(departments: List<Department>, query: String, navController: NavController) {
    val filteredDepartments = departments.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn {
        items(filteredDepartments) { department ->
            DepartmentItem(department = department, navController = navController)
        }
    }
}

@Composable
fun DepartmentItem(department: Department, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("department_detail/${department.name}/${department.id}/${department.leader}/${department.email}/${department.phone}/${department.address}")
            }
            .padding(8.dp)
    ) {
        Text(text = department.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
fun Useravatar(navController: NavController) {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(32.dp)
            .clickable {
                navController.navigate("update_detail")
            }
    )
}

@Composable
fun Staffitem(
    staff: Staff,
    isSelected: Boolean,
    onClick: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = staff.avatarURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(text = staff.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = staff.position, fontSize = 14.sp, color = Color.Gray)
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đơn vị: ${staff.department}", fontSize = 14.sp)
            Text("Email: ${staff.email}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
fun Stafflist(staffs: List<Staff>, query: String, navController: NavController) {
    var sortAscending by remember { mutableStateOf(true) } // Trạng thái sắp xếp

    val filteredStaffs = staffs.filter { it.name.contains(query, ignoreCase = true) }
    val sortedStaffs = if (sortAscending) {
        filteredStaffs.sortedBy { it.name.lowercase() } // A-Z
    } else {
        filteredStaffs.sortedByDescending { it.name.lowercase() } // Z-A
    }

    val groupedStaffs = ('A'..'Z').associateWith { letter ->
        sortedStaffs.filter { it.name.firstOrNull()?.uppercaseChar() == letter }
    }

    LazyColumn {
        groupedStaffs.forEach { (letter, staffList) ->
            item {
                Text(
                    text = letter.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                )
            }

            items(staffList) { staff ->
                Staffitem(
                    staff = staff,
                    isSelected = false,
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("staff", staff)
                        navController.navigate("DetailContactScreen")
                    },
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun Topbar(
    title: String,
    onLogoutClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Đăng xuất"
            )
        }
    }
}

@Composable
fun Searchbar(query: String, onQueryChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownOffset = DpOffset(0.dp, 10.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = dropdownOffset
                ) {
                    DropdownMenuItem(onClick = { /* Xử lý sắp xếp */ }) {
                        Text("Sắp xếp")
                    }
                    DropdownMenuItem(onClick = { /* Xử lý lọc */ }) {
                        Text("Lọc")
                    }
                }
            }
        }
    }
}

@Composable
fun Bottomnavigationbar(selectedTab: String, onTabSelected: (String) -> Unit) {
    BottomNavigation(backgroundColor = Color.White, contentColor = Color.Black) {
        BottomNavigationItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.department_icon),
                    contentDescription = "Đơn vị",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Đơn vị") Color(0xFF007BFE) else Color.Black,
                        BlendMode.SrcIn
                    )
                )
            },
            label = {
                Text(
                    "Đơn vị",
                    color = if (selectedTab == "Đơn vị") Color(0xFF007BFE) else Color.Black
                )
            },
            selected = selectedTab == "Đơn vị",
            onClick = { onTabSelected("Đơn vị") }
        )

        BottomNavigationItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.staff_icon),
                    contentDescription = "Giảng viên",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black,
                        BlendMode.SrcIn
                    )
                )
            },
            label = {
                Text(
                    "Giảng viên",
                    color = if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black
                )
            },
            selected = selectedTab == "Giảng viên",
            onClick = { onTabSelected("Giảng viên") }
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Sinh viên",
                    tint = if (selectedTab == "Sinh viên") Color(0xFF007BFE) else Color.Black
                )
            },
            label = {
                Text(
                    "Sinh viên",
                    color = if (selectedTab == "Sinh viên") Color(0xFF007BFE) else Color.Black
                )
            },
            selected = selectedTab == "Sinh viên",
            onClick = { onTabSelected("Sinh viên") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    val navController = rememberNavController()
    Directoryscreen(navController = navController)
}
