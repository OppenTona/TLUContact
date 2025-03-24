package com.example.tlucontact

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = ContactDatabase.getDatabase(applicationContext).contactDao()

        lifecycleScope.launch {
            // Chèn dữ liệu mẫu nếu trống
            if (dao.getAllStudents().isEmpty()) {
                studentList.forEach { (name, info) ->
                    dao.insertStudent(
                        Student(
                            name = name,
                            studentId = info["Mã SV"] ?: "",
                            className = info["Lớp"] ?: "",
                            email = info["Email"] ?: "",
                            phone = info["SĐT"] ?: "",
                            address = info["Địa chỉ"] ?: ""
                        )
                    )
                }
            }

            if (dao.getAllTeachers().isEmpty()) {
                teacherList.forEach { (name, info) ->
                    dao.insertTeacher(
                        Teacher(
                            name = name,
                            teacherId = info["Mã GV"] ?: "",
                            department = info["Bộ môn"] ?: "",
                            email = info["Email"] ?: "",
                            phone = info["SĐT"] ?: "",
                            address = info["Địa chỉ"] ?: ""
                        )
                    )
                }
            }

            if (dao.getAllDepartments().isEmpty()) {
                departmentList.forEach { (name, info) ->
                    dao.insertDepartment(
                        Department(
                            name = name,
                            code = info["Mã đơn vị"] ?: "",
                            leader = info["Trưởng đơn vị"] ?: "",
                            email = info["Email"] ?: "",
                            phone = info["SĐT"] ?: "",
                            address = info["Địa chỉ"] ?: ""
                        )
                    )
                }
            }
        }

        // ✅ Thiết lập Navigation với NavController
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "directory"
            ) {
                composable("directory") {
                    DirectoryScreen(navController = navController)
                }

                // Route cho Sinh viên
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
                        name = args.getString("name") ?: "",
                        studentId = args.getString("studentId") ?: "",
                        className = args.getString("className") ?: "",
                        email = args.getString("email") ?: "",
                        phone = args.getString("phone") ?: "",
                        address = args.getString("address") ?: ""
                    )
                }

                // Route cho Giảng viên
                composable(
                    route = "teacher_detail/{name}/{teacherId}/{department}/{email}/{phone}/{address}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("teacherId") { type = NavType.StringType },
                        navArgument("department") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("address") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val args = backStackEntry.arguments!!
                    DetailScreen(
                        navController = navController,
                        name = args.getString("name") ?: "",
                        studentId = args.getString("teacherId") ?: "",
                        className = args.getString("department") ?: "",
                        email = args.getString("email") ?: "",
                        phone = args.getString("phone") ?: "",
                        address = args.getString("address") ?: ""
                    )
                }

                // Route cho Đơn vị
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

// Dữ liệu mẫu
val departmentList = mapOf(
    "Khoa Cơ khí" to mapOf(
        "Mã đơn vị" to "KCK",
        "Trưởng đơn vị" to "TS. Nguyễn Cơ Khí",
        "Email" to "cokhi@tlu.edu.vn",
        "SĐT" to "0243123456",
        "Địa chỉ" to "Nhà C1 – Đại học Thủy lợi"
    ),
    "Khoa CNTT" to mapOf(
        "Mã đơn vị" to "KCNTT",
        "Trưởng đơn vị" to "TS. Trần Công Nghệ",
        "Email" to "cntt@tlu.edu.vn",
        "SĐT" to "0243654789",
        "Địa chỉ" to "Nhà A2 – Tầng 3"
    ),
    "Khoa Công trình" to mapOf(
        "Mã đơn vị" to "KCT",
        "Trưởng đơn vị" to "PGS. TS. Lê Công Trình",
        "Email" to "congtrinh@tlu.edu.vn",
        "SĐT" to "0243987654",
        "Địa chỉ" to "Nhà B1 – Tầng 2"
    ),
    "Khoa Điện - Điện tử" to mapOf(
        "Mã đơn vị" to "KDE",
        "Trưởng đơn vị" to "TS. Đỗ Văn Điện",
        "Email" to "dientu@tlu.edu.vn",
        "SĐT" to "0243344556",
        "Địa chỉ" to "Nhà C3 – Phòng 101"
    ),
    "Phòng CT&CTSV" to mapOf(
        "Mã đơn vị" to "PCTSV",
        "Trưởng đơn vị" to "TS. Nguyễn Văn CT",
        "Email" to "ctsv@tlu.edu.vn",
        "SĐT" to "0243666888",
        "Địa chỉ" to "Nhà A1 – Tầng 1"
    ),
    "Phòng Đào tạo" to mapOf(
        "Mã đơn vị" to "PDT",
        "Trưởng đơn vị" to "TS. Lê Đào Tạo",
        "Email" to "pdt@tlu.edu.vn",
        "SĐT" to "0243555123",
        "Địa chỉ" to "Nhà A1 – Tầng 2"
    ),
    "Phòng Khảo thí" to mapOf(
        "Mã đơn vị" to "PKT",
        "Trưởng đơn vị" to "TS. Bùi Khảo Thí",
        "Email" to "khaothi@tlu.edu.vn",
        "SĐT" to "0243777666",
        "Địa chỉ" to "Nhà A1 – Tầng 3"
    ),
    "Phòng Tài chính" to mapOf(
        "Mã đơn vị" to "PTC",
        "Trưởng đơn vị" to "TS. Trần Tài Chính",
        "Email" to "taichinh@tlu.edu.vn",
        "SĐT" to "0243666999",
        "Địa chỉ" to "Nhà A1 – Tầng 4"
    ),
    "TT Quốc tế" to mapOf(
        "Mã đơn vị" to "TTQT",
        "Trưởng đơn vị" to "TS. Lê Quốc Tế",
        "Email" to "ttqt@tlu.edu.vn",
        "SĐT" to "0243888222",
        "Địa chỉ" to "Nhà H1 – Tầng 1"
    ),
    "TT GDQP" to mapOf(
        "Mã đơn vị" to "TTQP",
        "Trưởng đơn vị" to "TS. Võ Quốc Phòng",
        "Email" to "ttqp@tlu.edu.vn",
        "SĐT" to "0243999333",
        "Địa chỉ" to "Ký túc xá khu B"
    ),
    "TT Tin học" to mapOf(
        "Mã đơn vị" to "TTH",
        "Trưởng đơn vị" to "ThS. Trần Tin Học",
        "Email" to "tinhoc@tlu.edu.vn",
        "SĐT" to "0243222111",
        "Địa chỉ" to "Nhà A2 – Tầng 5"
    ),
    "Thư viện" to mapOf(
        "Mã đơn vị" to "TV",
        "Trưởng đơn vị" to "ThS. Nguyễn Thư Viện",
        "Email" to "thuvien@tlu.edu.vn",
        "SĐT" to "0243001122",
        "Địa chỉ" to "Nhà Thư viện trung tâm"
    ),
    "Viện TNN" to mapOf(
        "Mã đơn vị" to "VTNN",
        "Trưởng đơn vị" to "GS. TS. Phạm Thủy",
        "Email" to "vtnn@tlu.edu.vn",
        "SĐT" to "0243666444",
        "Địa chỉ" to "Nhà D1 – Viện nghiên cứu"
    ),
    "Viện Công trình" to mapOf(
        "Mã đơn vị" to "VCT",
        "Trưởng đơn vị" to "GS. TS. Nguyễn Công",
        "Email" to "vct@tlu.edu.vn",
        "SĐT" to "0243222333",
        "Địa chỉ" to "Nhà D2 – Viện công trình"
    ),
    "Viện Thủy lợi" to mapOf(
        "Mã đơn vị" to "VTL",
        "Trưởng đơn vị" to "GS. TS. Lê Thủy Lợi",
        "Email" to "vtl@tlu.edu.vn",
        "SĐT" to "0243555666",
        "Địa chỉ" to "Nhà D3 – Viện thủy lợi"
    )
)


val studentList = mapOf(
    "Ngô Bá Khá" to mapOf(
        "Mã SV" to "2251060001",
        "Lớp" to "G1CNTT1",
        "Email" to "kha.ngoba@tlu.edu.vn",
        "SĐT" to "0987654321",
        "Địa chỉ" to "Bắc Ninh"
    ),
    "Nguyễn Văn A" to mapOf(
        "Mã SV" to "2251060002",
        "Lớp" to "G2CNTT1",
        "Email" to "a.nguyen@tlu.edu.vn",
        "SĐT" to "0911223344",
        "Địa chỉ" to "Hà Nội"
    ),
    "Nguyễn Văn Bình" to mapOf(
        "Mã SV" to "2251060003",
        "Lớp" to "G3CK1",
        "Email" to "binh.nv@tlu.edu.vn",
        "SĐT" to "0988123456",
        "Địa chỉ" to "Thái Bình"
    ),
    "Phạm Văn Bờ" to mapOf(
        "Mã SV" to "2251060004",
        "Lớp" to "G4XD1",
        "Email" to "bo.pv@tlu.edu.vn",
        "SĐT" to "0977654321",
        "Địa chỉ" to "Hải Dương"
    ),
    "Nguyễn Chính" to mapOf(
        "Mã SV" to "2251060005",
        "Lớp" to "G5ĐT1",
        "Email" to "chinh.nguyen@tlu.edu.vn",
        "SĐT" to "0933445566",
        "Địa chỉ" to "Nam Định"
    ),
    "Đỗ Hoài Chung" to mapOf(
        "Mã SV" to "2251060006",
        "Lớp" to "G6ĐT2",
        "Email" to "chung.dh@tlu.edu.vn",
        "SĐT" to "0909887766",
        "Địa chỉ" to "Hà Nam"
    ),
    "Nguyễn Danh" to mapOf(
        "Mã SV" to "2251060007",
        "Lớp" to "G7CN1",
        "Email" to "danh.nv@tlu.edu.vn",
        "SĐT" to "0966123456",
        "Địa chỉ" to "Ninh Bình"
    ),
    "Nguyễn Thị Đoàn" to mapOf(
        "Mã SV" to "2251060008",
        "Lớp" to "G8CN2",
        "Email" to "doan.nt@tlu.edu.vn",
        "SĐT" to "0911002233",
        "Địa chỉ" to "Thanh Hóa"
    )
)


val teacherList  = mapOf(
    "Lò Văn A" to mapOf(
        "Mã GV" to "GV001",
        "Bộ môn" to "Cơ khí",
        "Email" to "a.lv@tlu.edu.vn",
        "SĐT" to "0903344556",
        "Địa chỉ" to "Sơn La"
    ),
    "Nguyễn An" to mapOf(
        "Mã GV" to "GV002",
        "Bộ môn" to "CNTT",
        "Email" to "an.nguyen@tlu.edu.vn",
        "SĐT" to "0901234567",
        "Địa chỉ" to "Hà Nội"
    ),
    "Phạm Văn B" to mapOf(
        "Mã GV" to "GV003",
        "Bộ môn" to "Công trình",
        "Email" to "b.pv@tlu.edu.vn",
        "SĐT" to "0911223344",
        "Địa chỉ" to "Hải Phòng"
    ),
    "Lê Văn B" to mapOf(
        "Mã GV" to "GV004",
        "Bộ môn" to "Khoa học máy tính",
        "Email" to "b.lv@tlu.edu.vn",
        "SĐT" to "0988001122",
        "Địa chỉ" to "Hà Tĩnh"
    ),
    "Nguyễn Chung" to mapOf(
        "Mã GV" to "GV005",
        "Bộ môn" to "Điện - Điện tử",
        "Email" to "chung.ng@tlu.edu.vn",
        "SĐT" to "0977223344",
        "Địa chỉ" to "Quảng Ninh"
    ),
    "Đỗ Hoài C" to mapOf(
        "Mã GV" to "GV006",
        "Bộ môn" to "Tự động hóa",
        "Email" to "hoai.dh@tlu.edu.vn",
        "SĐT" to "0933667788",
        "Địa chỉ" to "Bắc Giang"
    ),
    "Nguyễn D" to mapOf(
        "Mã GV" to "GV007",
        "Bộ môn" to "Cơ sở hạ tầng",
        "Email" to "d.nguyen@tlu.edu.vn",
        "SĐT" to "0966554433",
        "Địa chỉ" to "Nghệ An"
    ),
    "Nguyễn Thị D" to mapOf(
        "Mã GV" to "GV008",
        "Bộ môn" to "Công nghệ phần mềm",
        "Email" to "d.nt@tlu.edu.vn",
        "SĐT" to "0909090909",
        "Địa chỉ" to "Hà Nội"
    )
)


// ========== UI ==========
@Composable
fun DirectoryScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }

    var selectedTab by remember { mutableStateOf("Sinh viên") }
    var query by remember { mutableStateOf("") }

    var students by remember { mutableStateOf(emptyList<Student>()) }
    var teachers by remember { mutableStateOf(emptyList<Teacher>()) }
    var departments by remember { mutableStateOf(emptyList<Department>()) }

    // Sử dụng LaunchedEffect để tải dữ liệu ngay lập tức
    LaunchedEffect(Unit) {
        students = dao.getAllStudents()
        teachers = dao.getAllTeachers()
        departments = dao.getAllDepartments()
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TopBar("Danh bạ $selectedTab")
            Spacer(Modifier.height(16.dp))
            SearchBar(query = query, onQueryChange = { query = it })
            Spacer(Modifier.height(16.dp))

            when (selectedTab) {
                "Sinh viên" -> StudentListFromDb(
                    initialStudents = students,
                    query = query,
                    onRefreshStudents = { students = dao.getAllStudents() },
                    onStudentClick = { student ->
                        val encodedName = URLEncoder.encode(student.name, StandardCharsets.UTF_8.toString())
                        val encodedClass = URLEncoder.encode(student.className, StandardCharsets.UTF_8.toString())
                        val encodedEmail = URLEncoder.encode(student.email, StandardCharsets.UTF_8.toString())
                        val encodedPhone = URLEncoder.encode(student.phone, StandardCharsets.UTF_8.toString())
                        val encodedAddress = URLEncoder.encode(student.address, StandardCharsets.UTF_8.toString())

                        navController.navigate(
                            "student_detail/$encodedName/${student.studentId}/$encodedClass/$encodedEmail/$encodedPhone/$encodedAddress"
                        )
                    }
                )

                "Giảng viên" -> TeacherListFromDb(
                    initialTeachers = teachers,
                    query = query,
                    onRefreshTeachers = { teachers = dao.getAllTeachers() },
                    onTeacherClick = { teacher ->
                        val encodedName = URLEncoder.encode(teacher.name, StandardCharsets.UTF_8.toString())
                        val encodedTeacherId = URLEncoder.encode(teacher.teacherId, StandardCharsets.UTF_8.toString())
                        val encodedDepartment = URLEncoder.encode(teacher.department, StandardCharsets.UTF_8.toString())
                        val encodedEmail = URLEncoder.encode(teacher.email, StandardCharsets.UTF_8.toString())
                        val encodedPhone = URLEncoder.encode(teacher.phone, StandardCharsets.UTF_8.toString())
                        val encodedAddress = URLEncoder.encode(teacher.address, StandardCharsets.UTF_8.toString())

                        navController.navigate(
                            "teacher_detail/$encodedName/$encodedTeacherId/$encodedDepartment/$encodedEmail/$encodedPhone/$encodedAddress"
                        )
                    }
                )

                "Đơn vị" -> DepartmentListFromDb(
                    initialDepartments = departments,
                    query = query,
                    onRefreshDepartments = { departments = dao.getAllDepartments() },
                    onDepartmentClick = { department ->
                        val encodedName = URLEncoder.encode(department.name, StandardCharsets.UTF_8.toString())
                        val encodedCode = URLEncoder.encode(department.code, StandardCharsets.UTF_8.toString())
                        val encodedLeader = URLEncoder.encode(department.leader, StandardCharsets.UTF_8.toString())
                        val encodedEmail = URLEncoder.encode(department.email, StandardCharsets.UTF_8.toString())
                        val encodedPhone = URLEncoder.encode(department.phone, StandardCharsets.UTF_8.toString())
                        val encodedAddress = URLEncoder.encode(department.address, StandardCharsets.UTF_8.toString())

                        navController.navigate(
                            "department_detail/$encodedName/$encodedCode/$encodedLeader/$encodedEmail/$encodedPhone/$encodedAddress"
                        )
                    }
                )
            }
        }
    }
}



@Composable
fun TopBar(title: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray, CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
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
        IconButton(onClick = {}) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.Blue)
        }
    }
}


// ==== Composables danh sách ====
@Composable
fun StudentListFromDb(
    initialStudents: List<Student>,
    query: String,
    onRefreshStudents: suspend () -> Unit,
    onStudentClick: (Student) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf(initialStudents) }
    var editing by remember { mutableStateOf<Student?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newClassName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

    fun reload() = scope.launch {
        onRefreshStudents()
        students = dao.getAllStudents()
    }

    val filteredStudents = students.filter {
        it.name.contains(query, ignoreCase = true)
    }

    LazyColumn {
        item {
            Text("Hồ sơ của bạn", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            StudentItem("Ngô Bá Khá")
        }
        filteredStudents.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { student ->
                StudentItem(
                    name = student.name,
                    onClick = { onStudentClick(student) }, // 👈 gọi callback để mở DetailScreen
                    onLongClick = {
                        editing = student
                        newName = student.name
                        newClassName = student.className
                        newEmail = student.email
                        newPhone = student.phone
                        newAddress = student.address
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && editing != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chỉnh sửa thông tin sinh viên") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Tên") })
                    OutlinedTextField(value = newClassName, onValueChange = { newClassName = it }, label = { Text("Lớp") })
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("SĐT") })
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.updateStudent(editing!!.copy(
                            name = newName,
                            className = newClassName,
                            email = newEmail,
                            phone = newPhone,
                            address = newAddress
                        ))
                        reload()
                        showDialog = false
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Huỷ") }
            }
        )
    }
}


@Composable
fun TeacherListFromDb(
    initialTeachers: List<Teacher>,
    query: String,
    onRefreshTeachers: suspend () -> Unit,
    onTeacherClick: (Teacher) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }
    val scope = rememberCoroutineScope()

    var teachers by remember { mutableStateOf(initialTeachers) }
    var editing by remember { mutableStateOf<Teacher?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newDepartment by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

    fun reload() = scope.launch {
        onRefreshTeachers()
        teachers = dao.getAllTeachers()
    }

    val filteredTeachers = teachers.filter {
        it.name.contains(query, ignoreCase = true)
    }

    LazyColumn {
        item {
            Text("Danh sách giảng viên", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        }
        filteredTeachers.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { teacher ->
                TeacherItem(
                    name = teacher.name,
                    onClick = { onTeacherClick(teacher) },
                    onLongClick = {
                        editing = teacher
                        newName = teacher.name
                        newDepartment = teacher.department
                        newEmail = teacher.email
                        newPhone = teacher.phone
                        newAddress = teacher.address
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && editing != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chỉnh sửa thông tin giảng viên") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Tên") })
                    OutlinedTextField(value = newDepartment, onValueChange = { newDepartment = it }, label = { Text("Bộ môn") })
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("SĐT") })
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.updateTeacher(editing!!.copy(
                            name = newName,
                            department = newDepartment,
                            email = newEmail,
                            phone = newPhone,
                            address = newAddress
                        ))
                        reload()
                        showDialog = false
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Huỷ") }
            }
        )
    }
}



@Composable
fun DepartmentListFromDb(
    initialDepartments: List<Department>,
    query: String,
    onRefreshDepartments: suspend () -> Unit,
    onDepartmentClick: (Department) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }
    val scope = rememberCoroutineScope()

    var departments by remember { mutableStateOf(initialDepartments) }
    var editing by remember { mutableStateOf<Department?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newCode by remember { mutableStateOf("") }
    var newLeader by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

    fun reload() = scope.launch {
        onRefreshDepartments()
        departments = dao.getAllDepartments()
    }

    val filteredDepartments = departments.filter {
        it.name.contains(query, ignoreCase = true)
    }

    LazyColumn {
        item {
            Text("Danh sách đơn vị", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
        }
        filteredDepartments.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { department ->
                DepartmentItem(
                    name = department.name,
                    onClick = { onDepartmentClick(department) },
                    onLongClick = {
                        editing = department
                        newName = department.name
                        newCode = department.code
                        newLeader = department.leader
                        newEmail = department.email
                        newPhone = department.phone
                        newAddress = department.address
                        showDialog = true
                    }
                )
            }
        }
    }

    if (showDialog && editing != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chỉnh sửa thông tin đơn vị") },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Tên đơn vị") })
                    OutlinedTextField(value = newCode, onValueChange = { newCode = it }, label = { Text("Mã đơn vị") })
                    OutlinedTextField(value = newLeader, onValueChange = { newLeader = it }, label = { Text("Trưởng đơn vị") })
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("SĐT") })
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Địa chỉ") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.updateDepartment(editing!!.copy(
                            name = newName,
                            code = newCode,
                            leader = newLeader,
                            email = newEmail,
                            phone = newPhone,
                            address = newAddress
                        ))
                        reload()
                        showDialog = false
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Huỷ") }
            }
        )
    }
}




// ==== Composable Items ====
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentItem(name: String,onClick: () -> Unit = {}, onLongClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeacherItem(name: String, onClick: () -> Unit = {}, onLongClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() } // 👈 Gọi onLongClick nếu có
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Normal) // 👈 Đồng nhất với StudentItem
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DepartmentItem(name: String, onClick: () -> Unit = {}, onLongClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() } // 👈 Gọi onLongClick nếu có
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Normal) // 👈 Đồng nhất với StudentItem
    }
}


@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    BottomNavigation(backgroundColor = Color.White, contentColor = Color.Black) {
        BottomNavigationItem(icon = { Text("🏢") }, label = { Text("Đơn vị") }, selected = selectedTab == "Đơn vị", onClick = { onTabSelected("Đơn vị") })
        BottomNavigationItem(icon = { Text("👨‍🏫") }, label = { Text("Giảng viên") }, selected = selectedTab == "Giảng viên", onClick = { onTabSelected("Giảng viên") })
        BottomNavigationItem(icon = { Text("🎓") }, label = { Text("Sinh viên") }, selected = selectedTab == "Sinh viên", onClick = { onTabSelected("Sinh viên") })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    val navController = rememberNavController()
    DirectoryScreen(navController = navController)
}


// ========== ROOM ENTITIES & DAO ==========

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val studentId: String,
    val className: String,
    val email: String,
    val phone: String,
    val address: String
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val teacherId: String,
    val department: String,
    val email: String,
    val phone: String,
    val address: String
)

@Entity(tableName = "units")
data class Department(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String,
    val leader: String,
    val email: String,
    val phone: String,
    val address: String
)



@Dao
interface ContactDao {
    // Student
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertStudent(student: Student)
    @Query("SELECT * FROM students ORDER BY name ASC") suspend fun getAllStudents(): List<Student>
    @Update suspend fun updateStudent(student: Student)
    @Delete suspend fun deleteStudent(student: Student)

    // Teacher
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertTeacher(teacher: Teacher)
    @Query("SELECT * FROM teachers ORDER BY name ASC") suspend fun getAllTeachers(): List<Teacher>
    @Update suspend fun updateTeacher(teacher: Teacher)
    @Delete suspend fun deleteTeacher(teacher: Teacher)

    // Department
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertDepartment(unit: Department)
    @Query("SELECT * FROM units ORDER BY name ASC") suspend fun getAllDepartments(): List<Department>
    @Update suspend fun updateDepartment(unit: Department)
    @Delete suspend fun deleteDepartment(unit: Department)
}

@Database(entities = [Student::class, Teacher::class, Department::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile private var INSTANCE: ContactDatabase? = null

        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
