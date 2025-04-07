package com.example.tlucontact.view

import android.content.Intent
import android.net.Uri
import com.example.tlucontact.DetailScreen
import com.example.tlucontact.MainActivity
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberAsyncImagePainter
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
            // ViewModel dùng chung
            val staffViewModel: StaffViewModel = viewModel()

            // 👉 THÊM DÒNG NÀY
            val selectedStaff by staffViewModel.selectedStaff.collectAsState()

            NavHost(
                navController = navController,
                startDestination = "directory"
            ) {
                composable(route = "update_detail") {
                    val staffViewModel: StaffViewModel = viewModel()  // Lấy ViewModel
                    UpdateDetailScreen(
                        staff = selectedStaff,
                        onBack = { navController.popBackStack() },
                        onSave = { updatedStaff ->
                            staffViewModel.updateStaffInfo(updatedStaff)  // Gọi hàm update trong ViewModel
                            navController.popBackStack()  // Quay lại sau khi lưu
                        }
                    )
                }



                composable("directory") {
                    Directoryscreen(
                        navController = navController,
                        staffViewModel = staffViewModel
                    )
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
                    DetailStudentScreen(
                        student = Student(
                            fullNameStudent = args.getString("name") ?: "",
                            studentID = args.getString("studentId") ?: "",
                            className = args.getString("className") ?: "",
                            email = args.getString("email") ?: "",
                            phone = args.getString("phone") ?: "",
                            address = args.getString("address") ?: ""
                        ),
                        onBack = { navController.popBackStack() }
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
                    route = "department_detail/{name}/{id}/{leader}/{email}/{phone}/{address}?screenTitle={screenTitle}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("id") { type = NavType.StringType },
                        navArgument("leader") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("address") { type = NavType.StringType },
                        navArgument("screenTitle") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val args = backStackEntry.arguments!!
                    val department = Department(
                        name = Uri.decode(args.getString("name") ?: ""),
                        id = Uri.decode(args.getString("id") ?: ""),
                        leader = Uri.decode(args.getString("leader") ?: ""),
                        email = Uri.decode(args.getString("email") ?: ""),
                        phone = Uri.decode(args.getString("phone") ?: ""),
                        address = Uri.decode(args.getString("address") ?: "")
                    )

                    DepartmentDetailView(
                        department = department,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun Directoryscreen(
    navController: NavController,
    staffViewModel: StaffViewModel, // 👈 không khởi tạo ở đây nữa
    studentViewModel: StudentViewModel = StudentViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Giảng viên") }
    var query by remember { mutableStateOf("") }
    var isFilterActive by remember { mutableStateOf(false) } // Thêm trạng thái lọc
    var selectedDepartment by remember { mutableStateOf("") } // Thêm trạng thái đơn vị đã chọn
    val staffs by staffViewModel.staffList.collectAsState()
    val students by studentViewModel.studentList.collectAsState()

    val departmentRepository = DepartmentRepository()
    val departmentViewModel: DepartmentViewModel = viewModel(
        factory = DepartmentViewModelFactory(departmentRepository)
    )
    val departments by departmentViewModel.departmentList.collectAsState()



    val conText = LocalContext.current
    val userLoginEmail = SessionManager(context).getUserLoginEmail()
    val selectedStaff by staffViewModel.selectedStaff.collectAsState()

    LaunchedEffect(userLoginEmail) {
        if (!userLoginEmail.isNullOrBlank()) {
            staffViewModel.setStaffByEmail(userLoginEmail)
        }
    }


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
            Searchbar(query = query, onQueryChange = { query = it }, selectedTab = selectedTab, onFilterClick = { isFilterActive = true })
            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Useravatar(navController)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Hồ sơ của bạn", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = selectedStaff?.name ?: "Chưa có tên",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    //Text("Nguyễn Thị Mai Hương", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == "Giảng viên") {
                Stafflist(
                    staffs = staffs,
                    query = query,
                    navController = navController,
                    isFilterActive = isFilterActive,
                    selectedDepartment = selectedDepartment // Truyền giá trị của selectedDepartment
                )
            } else if (selectedTab == "Đơn vị") {
                DepartmentList(departments = departments, query = query, navController = navController)
            } else if (selectedTab == "Sinh viên") {
                StudentList(students = students, query = query, navController = navController)
            }
        }
    }
}


@Composable
fun StudentList(
    students: List<Student>,
    query: String,
    navController: NavController,
    studentViewModel: StudentViewModel = viewModel()
) {
    // Sử dụng trạng thái sắp xếp từ ViewModel thay vì biến local
    val sortAscending by studentViewModel.sortAscending.collectAsState()
    val filterMode by studentViewModel.filterMode.collectAsState()

    val filteredStudents = students.filter { it.fullNameStudent.contains(query, ignoreCase = true) }
    val sortedStudents = if (sortAscending) {
        filteredStudents.sortedBy { it.fullNameStudent.lowercase() }
    } else {
        filteredStudents.sortedByDescending { it.fullNameStudent.lowercase() }
    }

    Column {
        if (filterMode == "ByClass") {
            // Hiển thị danh sách sinh viên theo lớp
            val groupedByClass = sortedStudents.groupBy { it.className }

            LazyColumn {
                groupedByClass.forEach { (className, studentList) ->
                    // Hiển thị tên lớp làm header
                    item {
                        Text(
                            text = className.ifEmpty { "Không có lớp" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEAEAEA))
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }

                    // Hiển thị danh sách sinh viên thuộc lớp
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
        } else {
            // Hiển thị dạng nhóm theo chữ cái đầu (giữ nguyên code cũ)
            val groupedStudents = sortedStudents.groupBy { it.fullNameStudent.firstOrNull()?.uppercaseChar() ?: '#' }

            LazyColumn {
                groupedStudents.forEach { (letter, studentList) ->
                    if (letter != '#') { // Chỉ hiển thị header nếu có sinh viên bắt đầu bằng chữ cái
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
                Divider(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.wrapContentWidth(Alignment.Start) // Giới hạn chiều rộng theo nội dung và căn trái
                )
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: ${student.email}", fontSize = 14.sp)
            Text("Số điện thoại: ${student.phone}", fontSize = 14.sp)
            Text("Địa chỉ: ${student.address}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

//        Divider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}



@Composable
fun DepartmentList(departments: List<Department>, query: String, navController: NavController) {
    val filteredDepartments = departments.filter { it.name.contains(query, ignoreCase = true) }
    val groupedDepartments = filteredDepartments.groupBy { it.name.first().uppercaseChar() }

    LazyColumn {
        ('A'..'Z').forEach { letter ->
            if (groupedDepartments.containsKey(letter)) {
                item {
                    Text(
                        text = letter.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray.copy(alpha = 0.6f), // Màu xám nhạt hơn
                        modifier = Modifier.padding(3.dp)
                    )
                }

                items(groupedDepartments[letter]!!) { department ->
                    DepartmentItem(department = department, navController = navController)
                }
            }
        }
    }
}

@Composable
fun DepartmentItem(department: Department, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("Navigation", "Navigating to department_detail with: ${department.name}, ${department.id}, ${department.phone}")
                navController.navigate(
                    "department_detail/" +
                            "${Uri.encode(department.name)}/" +
                            "${Uri.encode(department.id)}/" +
                            "${Uri.encode(department.leader)}/" +
                            "${Uri.encode(department.email)}/" +
                            "${Uri.encode(department.phone)}/" +
                            "${Uri.encode(department.address)}?screenTitle=${Uri.encode(department.name)}"
                )
            }
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically // Căn chỉnh theo chiều dọc
    ) {
        Image(
            painter = if (department.photoURL.isNullOrEmpty()) {
                painterResource(id = R.drawable.thuyloi) // Ảnh mặc định nếu không có photoURL
            } else {
                rememberAsyncImagePainter(department.photoURL) // Tải ảnh từ URL
            },
            contentDescription = "Ảnh đại diện",
            modifier = Modifier
                .size(32.dp) // Kích thước ảnh
                .clip(CircleShape) // Bo tròn ảnh
        )

        Spacer(modifier = Modifier.width(12.dp)) // Khoảng cách giữa ảnh và text

        Column {
            Text(text = department.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar tròn, nhỏ
            AsyncImage(
                model = staff.avatarURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = staff.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = staff.position,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đơn vị: ${staff.department}", fontSize = 14.sp)
            Text("Email: ${staff.email}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Divider dưới mỗi item
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}


@Composable
fun Stafflist(
    staffs: List<Staff>,
    query: String,
    navController: NavController,
    isFilterActive: Boolean,
    selectedDepartment: String

) {
    var sortAscending by remember { mutableStateOf(true) } // Trạng thái sắp xếp

    val filteredStaffs = if (isFilterActive) {
        staffs.filter {
            it.name.contains(query, ignoreCase = true) &&
                    it.department.contains(selectedDepartment, ignoreCase = true)
        }
    } else {
        staffs.filter { it.name.contains(query, ignoreCase = true) }
    }

    val sortedStaffs = if (sortAscending) {
        filteredStaffs.sortedBy { it.name.lowercase() } // A-Z
    } else {
        filteredStaffs.sortedByDescending { it.name.lowercase() } // Z-A
    }

    val groupedStaffsByDepartment = sortedStaffs.groupBy { it.department }
    val groupedStaffsByName = ('A'..'Z').associateWith { letter ->
        sortedStaffs.filter { it.name.firstOrNull()?.uppercaseChar() == letter }
    }

    LazyColumn {
        if (isFilterActive) {
            groupedStaffsByDepartment.forEach { (department, staffList) ->
                item {
                    Text(
                        text = department,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                items(staffList) { staff ->
                    Staffitem(staff = staff, isSelected = false, onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("staff", staff)
                        navController.navigate("DetailContactScreen")
                    }, navController = navController)
                }
            }
        } else {
            groupedStaffsByName.forEach { (letter, staffList) ->
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
                    Staffitem(staff = staff, isSelected = false, onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("staff", staff)
                        navController.navigate("DetailContactScreen")
                    }, navController = navController)
                }
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
fun Searchbar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedTab: String,
    onFilterClick: () -> Unit, // Đưa tham số này lên đây
    studentViewModel: StudentViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }
    val dropdownOffset = DpOffset(0.dp, 15.dp)
    val filterMenuOffset = DpOffset((160).dp, 165.dp) // Điều chỉnh vị trí của menu lọc

    // Lấy trạng thái sắp xếp hiện tại
    val studentSortAscending by studentViewModel.sortAscending.collectAsState()
    // Xác định trạng thái sắp xếp dựa trên tab đang chọn
    val currentSortAscending = when (selectedTab) {
        "Sinh viên" -> studentSortAscending
        else -> true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, CircleShape)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(30.dp).padding(start = 10.dp) // Thêm padding bên trái
            )
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
                    DropdownMenuItem(onClick = {
                        // Xử lý sắp xếp dựa trên tab đang chọn
                        when (selectedTab) {
                            "Sinh viên" -> studentViewModel.toggleSortOrder()
                        }
                        expanded = false
                    }) {
                        Text(if (currentSortAscending) "Sắp xếp Z-A" else "Sắp xếp A-Z")
                    }
                    DropdownMenuItem(onClick = { expandedFilter = true }) {
                        Text("Lọc")
                    }
                    if (expandedFilter) {
                        DropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false },
                            offset = filterMenuOffset
                        ) {
                            when (selectedTab) {
                                "Sinh viên" -> {
                                    DropdownMenuItem(onClick = {
                                        // Khi nhấn "Lọc Theo Lớp"
                                        studentViewModel.setFilterMode("ByClass")
                                        expanded = false
                                        expandedFilter = false
                                        onFilterClick()
                                    }) {
                                        Text("Lọc Theo Lớp")
                                    }
                                    DropdownMenuItem(onClick = {
                                        // Khi nhấn "Lọc Theo Tên"
                                        studentViewModel.setFilterMode("ByName")
                                        expanded = false
                                        expandedFilter = false
                                        onFilterClick()
                                    }) {
                                        Text("Lọc Theo Tên")
                                    }
                                }
                                "Giảng viên" -> {
                                    DropdownMenuItem(onClick = {
                                        // Thực hiện hành động lọc (ví dụ: cập nhật selectedDepartment)
                                        // Sau đó gọi onFilterClick để kích hoạt hiển thị đã lọc
                                        // Ví dụ đơn giản:
                                        // onQueryChange("") // Có thể bạn muốn lọc theo đơn vị cụ thể
                                        // CẦN THÊM LOGIC CẬP NHẬT selectedDepartment Ở ĐÂY
                                        onFilterClick()
                                    }) {
                                        Text("Theo Đơn vị")
                                    }
                                }
                                "Đơn vị" -> {
                                    DropdownMenuItem(onClick = { /* Xử lý lọc theo khoa */ onFilterClick() }) {
                                        Text("Theo Khoa")
                                    }
                                    DropdownMenuItem(onClick = { /* Xử lý lọc theo ngành */ onFilterClick() }) {
                                        Text("Theo Ngành")
                                    }
                                }
                            }
                        }
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
    val staffViewModel = StaffViewModel() // giả lập trong preview
    Directoryscreen(navController = navController, staffViewModel = staffViewModel)
}

