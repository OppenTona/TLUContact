package com.example.tlucontact.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.tlucontact.DetailScreen
import com.example.tlucontact.MainActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.example.tlucontact.DepartmentItem
import com.example.tlucontact.R
import com.example.tlucontact.StudentItem
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.model.Staff
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.data.repository.DepartmentRepository
import com.example.tlucontact.data.repository.SessionManager
import com.example.tlucontact.viewmodel.DepartmentViewModel
import com.example.tlucontact.viewmodel.DepartmentViewModelFactory
import com.example.tlucontact.viewmodel.GuestViewModel
import com.example.tlucontact.viewmodel.LogoutViewModel
import com.example.tlucontact.viewmodel.StaffViewModel
import com.example.tlucontact.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    navControllerLogout: NavController,
) {
    // Tạo đối tượng NavController dùng để điều hướng giữa các màn hình trong Compose
    // rememberNavController sẽ nhớ lại NavController khi giao diện được recomposed
    val navController = rememberNavController()

    // Tạo instance của DepartmentRepository
    val repository = DepartmentRepository() // Thay thế bằng cách tạo instance thực tế của repository

    // Sử dụng DepartmentViewModelFactory để tạo DepartmentViewModel
    val departmentViewModel: DepartmentViewModel = viewModel(factory = DepartmentViewModelFactory(repository))

    // ViewModel dùng chung

    val staffViewModel: StaffViewModel = viewModel()    // Tạo hoặc lấy ViewModel có kiểu StaffViewModel, ViewModel này được dùng để quản lý dữ liệu và logic liên quan đến giảng viên, viewModel() sẽ tự động gán theo vòng đời của composable
    val studentViewModel: StudentViewModel = viewModel() // Tạo hoặc lấy ViewModel có kiểu StudentViewModel, ViewModel này được dùng để quản lý dữ liệu và logic liên quan đến sinh viên
    val guestViewModel: GuestViewModel = viewModel()
    val logoutViewModel: LogoutViewModel = viewModel() // Sử dụng ViewModel
    val logoutState by logoutViewModel.logoutState.collectAsState() // Theo dõi trạng thái đăng xuất

    val selectedStaff by staffViewModel.selectedStaff.collectAsState()
    val selectedStudent by studentViewModel.selectedStudent.collectAsState() // Lấy thông tin sinh viên đang được chọn từ ViewModel
    val selectedGuest by guestViewModel.selectedGuest.collectAsState()
    // LaunchedEffect sẽ chạy khối code bên trong khi giá trị logoutState thay đổi
    LaunchedEffect(logoutState) {
        // Nếu logoutState.first == true → đăng xuất thành công
        if (logoutState.first) {
            // Điều hướng sang màn hình đăng nhập (login)
            navControllerLogout.navigate("login") {
                // Xóa toàn bộ backstack (xóa hết các màn hình trước đó)
                popUpTo(0) { inclusive = true }
            }
        }
        // Nếu logoutState.second khác null → có lỗi xảy ra khi đăng xuất
        else if (logoutState.second != null) {
            // Hiển thị thông báo lỗi bằng Toast
            Toast.makeText(
                navController.context,
                "Lỗi: ${logoutState.second}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    NavHost(
        navController = navController,
        startDestination = "directory"
    ) {
        composable(route = "update_detail_student") {
            // Lấy hoặc tạo một instance của StudentViewModel để quản lý dữ liệu và logic của màn hình cập nhật sinh viên
            val studentViewModel: StudentViewModel = viewModel()
            UpdateDetailStudentScreen(
                student = selectedStudent, // Truyền vào sinh viên được chọn để hiển thị thông tin cần cập nhật
                onBack = { navController.popBackStack() }, // Callback khi người dùng nhấn nút quay lại, sẽ điều hướng quay lại màn hình trước
                onSave = { updatedStudent ->  // Callback khi người dùng nhấn nút "Lưu" sau khi chỉnh sửa thông tin sinh viên
                    studentViewModel.updateStudentInfo(updatedStudent) // Gọi hàm cập nhật thông tin sinh viên trong ViewModel với dữ liệu mới
                    navController.popBackStack() // Sau khi cập nhật xong thì quay trở lại màn hình trước đó
                    },
                viewModel = studentViewModel,  // Truyền ViewModel vào màn hình để sử dụng trong giao diện
                navController = navController // Truyền NavController để có thể điều hướng trong composable UpdateDetailStudentScreen
            )
        }
        // Định nghĩa một composable cho route "update_detail"
        composable(route = "update_detail") {

            // Lấy instance của StaffViewModel để dùng trong màn hình chỉnh sửa
            val staffViewModel: StaffViewModel = viewModel()

            // Gọi composable UpdateDetailScreen, truyền vào các tham số cần thiết
            UpdateDetailScreen(
                staff = selectedStaff, // Truyền đối tượng giảng viên đang được chọn để hiển thị thông tin
                onBack = {
                    navController.popBackStack() // Khi nhấn nút quay lại → điều hướng về màn hình trước đó
                    },
                onSave = { updatedStaff ->
                    // Khi nhấn nút lưu → gọi hàm update trong ViewModel để cập nhật thông tin giảng viên
                    staffViewModel.updateStaffInfo(updatedStaff)

                // Quay lại màn hình trước sau khi lưu (nếu muốn kích hoạt dòng này thì bỏ comment)
                // navController.popBackStack()
                }
            )
        }

        composable(route = "update_detail_guest") {
            val guestViewModel: GuestViewModel = viewModel()
            val guestState by guestViewModel.selectedGuest.collectAsState()

            UpdateDetailGuestScreen(
                guest = guestState,
                onBack = { navController.popBackStack() },
                viewModel = guestViewModel
            )
        }

        composable("directory") {
            Directoryscreen(
                navController = navController,
                staffViewModel = staffViewModel,
                studentViewModel = studentViewModel,
                guestViewModel = guestViewModel,
                logoutViewModel = logoutViewModel
            )
        }


        composable(
            // Định nghĩa đường dẫn (route) có chứa các tham số truyền vào
            route = "student_detail/{name}/{studentId}/{className}/{email}/{phone}/{address}",
            // Định nghĩa đường dẫn (route) có chứa các tham số truyền vào
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("studentId") { type = NavType.StringType },
                navArgument("className") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("address") { type = NavType.StringType }
            )
        ) { backStackEntry -> // Khối xử lý khi route này được điều hướng đến
            val args = backStackEntry.arguments!! // Lấy ra Bundle chứa các tham số đã truyền vào
            // Gọi màn hình chi tiết sinh viên, truyền vào một đối tượng Student được tạo từ các tham số
            DetailStudentScreen(
                student = Student(
                    fullNameStudent = args.getString("name") ?: "",
                    studentID = args.getString("studentId") ?: "",
                    className = args.getString("className") ?: "",
                    email = args.getString("email") ?: "",
                    phone = args.getString("phone") ?: "",
                    address = args.getString("address") ?: ""
                ),
                // Hàm xử lý khi nhấn nút quay lại, sẽ pop khỏi backstack
                onBack = { navController.popBackStack() }
            )
        }

        // Định nghĩa một composable cho màn hình "DetailContactScreen"
        composable(route = "DetailContactScreen") {

            // Lấy đối tượng staff được truyền từ màn hình trước đó thông qua savedStateHandle
            // Nếu không có (null) thì tạo một Staff rỗng để tránh lỗi
            val staff = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Staff>("staff")
                ?: Staff("", "", "", "") // Tránh lỗi null bằng cách gán giá trị mặc định

            // Gọi màn hình chi tiết, truyền dữ liệu staff vào
            DetailContactScreen(
                staff = staff,
                onBack = {
                    navController.popBackStack() // Khi nhấn nút quay lại → điều hướng về màn hình trước
                },
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
                onBack = { navController.popBackStack() },
                onEditClick = {}
            )
        }
    }
}

@Composable
fun Directoryscreen(
    navController: NavController, //điều hướng
    //Đảm bảo tái sử dụng ViewModel, tránh việc mỗi màn hình lại tạo mới.
    //Truy cập các hàm như updateStaffInfo(), getStaffById(), hoặc luồng dữ liệu như staffList.
    staffViewModel: StaffViewModel,
    studentViewModel: StudentViewModel,
    guestViewModel: GuestViewModel = viewModel(),
    logoutViewModel: LogoutViewModel = viewModel()
) {
    // Lấy context hiện tại của ứng dụng (dùng để hiển thị Toast, gọi Intent,...)
    val context = LocalContext.current

    // Biến lưu trạng thái tab đang được chọn, mặc định là "Giảng viên"
    // Dùng để hiển thị nội dung phù hợp theo tab (ví dụ: "Giảng viên", "Sinh viên", v.v.)
    var selectedTab by remember { mutableStateOf("Giảng viên") }

    // Biến lưu nội dung tìm kiếm hiện tại trong ô tìm kiếm
    // Khi người dùng nhập text vào ô tìm kiếm, query sẽ thay đổi
    var query by remember { mutableStateOf("") }
    var staffFilterMode by remember { mutableStateOf("All") } // Mặc định là "Tất cả"
    // Biến dùng để xác định xem bộ lọc có đang được bật hay không
    // true → đang bật lọc; false → không lọc
    var isFilterActive by remember { mutableStateOf(false) }

    var selectedDepartment by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf("") }


    val departmentRepository = DepartmentRepository()
    val departmentViewModel: DepartmentViewModel = viewModel(
        factory = DepartmentViewModelFactory(departmentRepository)
    )
    val filteredDepartments by departmentViewModel.filteredDepartments.collectAsState() // Lấy danh sách đã lọc

    val departments by departmentViewModel.departmentList.collectAsState()

    // Lấy email người dùng đã đăng nhập từ SessionManager (lưu trong SharedPreferences hoặc tương tự)
    val userLoginEmail = SessionManager(context).getUserLoginEmail()

    // Lấy danh sách giảng viên từ ViewModel dưới dạng State để tự động cập nhật UI khi dữ liệu thay đổi
    val staffs by staffViewModel.staffList.collectAsState()

    // Lấy thông tin giảng viên đang được chọn từ ViewModel
    // Thường dùng để truyền sang màn hình chi tiết hoặc chỉnh sửa
    val selectedStaff by staffViewModel.selectedStaff.collectAsState()

    val selectedStudent by studentViewModel.selectedStudent.collectAsState()
    val selectedGuest by guestViewModel.selectedGuest.collectAsState()

    val students by studentViewModel.studentList.collectAsState()


    LaunchedEffect(userLoginEmail) {
        if (!userLoginEmail.isNullOrBlank()) {
            when {
                userLoginEmail.endsWith("@e.tlu.edu.vn") -> {
                    Log.d("Navigation", "Navigating to update_detail_student")
                    studentViewModel.setStudentByEmail(userLoginEmail)
                    studentViewModel.fetchStudents(userLoginEmail)
                }
                userLoginEmail.endsWith("@tlu.edu.vn") -> {
                    Log.d("Navigation", "Navigating to update_detail_staff")
                    staffViewModel.setStaffByEmail(userLoginEmail)
                    studentViewModel.fetchStudents(userLoginEmail)
                }
                else -> {
                    Log.d("Navigation", "Navigating to update_detail_guest")
                    guestViewModel.fetchGuestByEmail(userLoginEmail)
                    staffViewModel.setStaffByEmail(userLoginEmail)
                    studentViewModel.fetchStudents(userLoginEmail)
                }
            }
        }
    }


    // Scaffold là layout cơ bản có top bar, bottom bar, FAB, content...
    Scaffold(
        // Thanh điều hướng dưới cùng (BottomNavigationBar)
        bottomBar = {
            Bottomnavigationbar(selectedTab) { newTab ->
                // Khi người dùng chọn tab mới → cập nhật selectedTab
                selectedTab = newTab
            }
        }
    ) { padding ->

        // Phần nội dung chính, được bọc trong Column
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding) // padding do Scaffold cung cấp
                .padding(16.dp)   // padding cố định bên trong
        ) {

            // Topbar hiển thị tiêu đề và nút logout
            Topbar(
                title = "Danh bạ $selectedTab", // Tiêu đề thay đổi theo tab
                onLogoutClick = { logoutViewModel.logout() } // Gọi logout khi nhấn nút
            )

            Spacer(Modifier.height(16.dp)) // Khoảng cách

            // Thanh tìm kiếm, có thể hiển thị thêm lọc nếu là Đơn vị
            Searchbar(
                query = query, // Nội dung tìm kiếm hiện tại
                onQueryChange = { query = it }, // Cập nhật query khi người dùng nhập
                selectedTab = selectedTab, // Biết đang ở tab nào để xử lý đúng
                onFilterClick = { isFilterActive = true }, // Mở lọc khi nhấn icon lọc

                // Chỉ truyền DepartmentViewModel nếu đang ở tab "Đơn vị"
                departmentViewModel = if (selectedTab == "Đơn vị") departmentViewModel else null,

                onDepartmentSortOrderChange = { newSortOrder ->
                    // (Nếu muốn) cập nhật trạng thái sắp xếp
                    // departmentViewModel.setSortAscending(newSortOrder)
                },
                onStaffFilterChange = { mode ->
                    staffFilterMode = mode
                }
            )

            Spacer(Modifier.height(8.dp))

            // Hiển thị avatar người dùng và tên hồ sơ hiện tại (giảng viên, sinh viên, hoặc khách)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Useravatar(navController, guestViewModel) // Hiển thị avatar

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text("Hồ sơ của bạn", fontSize = 14.sp, color = Color.Gray)

                    // Hiển thị tên người dùng tương ứng với loại tài khoản (ưu tiên theo thứ tự)
                    Text(
                        text = selectedStaff?.name
                            ?: selectedStudent?.fullNameStudent
                            ?: selectedGuest?.name
                            ?: "Chưa có tên",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Hiển thị danh sách tương ứng theo tab được chọn
            when (selectedTab) {
                "Giảng viên" -> Stafflist(
                    staffs = staffs,
                    query = query,
                    navController = navController,
                    isFilterActive = isFilterActive,
                    selectedDepartment = selectedDepartment,
                    selectedPosition = selectedPosition,
                    staffViewModel = staffViewModel,
                    staffFilterMode = staffFilterMode
                )


                "Đơn vị" -> DepartmentList(
                    departmentsFlow = departmentViewModel.filteredDepartments,
                    query = query,
                    navController = navController,
                    departmentViewModel = departmentViewModel,
                    onDepartmentClick = { department -> }
                )

                "Sinh viên" -> StudentList(
                    students = students, // Danh sách sinh viên
                    query = query, // Từ khóa tìm kiếm
                    navController = navController // Điều hướng
                )
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
    val filterMode by studentViewModel.filterMode.collectAsState() // Lấy chế độ lọc từ ViewModel
    // Lọc danh sách sinh viên theo tên
    val filteredStudents = students.filter { student ->
        student.fullNameStudent.contains(query, ignoreCase = true) || // Tìm kiếm theo tên
                student.studentID.equals(query, ignoreCase = true) // Tìm kiếm theo mã sinh viên
    }
    // Sắp xếp danh sách sinh viên theo tên
    val sortedStudents = if (sortAscending) {
        // Nếu đang sắp xếp tăng dần ừ A-Z
        filteredStudents.sortedBy { it.fullNameStudent.lowercase() }
    } else {
        // Nếu đang sắp xếp giảm dần Z-A
        filteredStudents.sortedByDescending { it.fullNameStudent.lowercase() }
    }

    Column {
        // Kiểm tra nếu người dùng đang lọc theo lớp
        if (filterMode == "ByClass") {
            // Hiển thị danh sách sinh viên theo lớp
            val groupedByClass = sortedStudents.groupBy { it.className }

            LazyColumn {
                // Duyệt qua từng sinh viên theo lớp
                groupedByClass.forEach { (className, studentList) ->
                    // Hiển thị tên lớp làm header
                    item {
                        Text(
                            text = className.ifEmpty { "Không có lớp" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 16.dp)
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
            // Nếu không lọc theo lớp thì hiển thị danh sách sinh viên theo chữ cái đầu tiên của tên
            val groupedStudents = sortedStudents.groupBy {
                it.fullNameStudent
                    .trim()
                    .split(" ")
                    .lastOrNull()
                    ?.firstOrNull()
                    ?.uppercaseChar() ?: '#'
            }

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
    student: Student, // Dữ liệu sinh viên cụ thể
    isSelected: Boolean, // Nếu được chọn, hiển thị thêm thông tin
    onClick: () -> Unit, // Callback khi người dùng click vào item
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Khi nhấn vào item, gọi hàm onClick
            .padding(8.dp) // Khoảng cách bên trong item
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Sắp xếp các thành phần bên trong theo chiều ngang, căn giữa theo chiều dọc
        ) {
            // Hiển thị ảnh đại diện của sinh viên.
            AsyncImage(
                model = student.photoURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
                    .clip(CircleShape) // Bo tròn ảnh
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                // Hiển thị tên sinh viên
                Text(text = student.fullNameStudent, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
//                Text(text = student.className, fontSize = 14.sp, color = Color.Gray)
                // Thêm khoảng cách 4dp giữa Text và Divider
                Spacer(modifier = Modifier.height(4.dp))

                // Kẻ đường gạch chân dưới tên sinh viên
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
fun DepartmentList(
    departmentsFlow: StateFlow<List<Department>>,
    query: String,
    navController: NavController,
    departmentViewModel: DepartmentViewModel,
    onDepartmentClick: (Department) -> Unit // Thêm lambda xử lý click
) {
    val departments by departmentsFlow.collectAsState()
    val sortAscending by departmentViewModel.sortAscending.collectAsState()

    val filteredDepartments = remember(departments, query) {
        departments.filter { it.name.contains(query, ignoreCase = true) }
    }

    val sortedDepartments = remember(filteredDepartments, sortAscending) {
        if (sortAscending) {
            filteredDepartments.sortedBy { it.name.lowercase() }
        } else {
            filteredDepartments.sortedByDescending { it.name.lowercase() }
        }
    }

    val groupedDepartments = remember(sortedDepartments) {
        sortedDepartments.groupBy { it.name.first().uppercaseChar() }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        ('A'..'Z').forEach { letter ->
            if (groupedDepartments.containsKey(letter)) {
                item {
                    Text(
                        text = letter.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.padding(3.dp)
                    )
                }

                items(groupedDepartments[letter]!!) { department ->
                    DepartmentItem(
                        department = department,
                        navController = navController,
                        onClick = { onDepartmentClick(department) } // Gọi lambda khi click
                    )
                }
            }
        }
    }
}

@Composable
fun DepartmentItem(department: Department, navController: NavController, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Sử dụng onClick từ tham số
            .clickable {
                Log.d(
                    "Navigation",
                    "Navigating to department_detail with: ${department.name}, ${department.id}, ${department.phone}"
                )
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
fun Useravatar(navController: NavController, guestViewModel: GuestViewModel) {
    // Lấy context hiện tại trong Compose (dùng cho các thao tác ngoài UI như lấy email)
    val conText = LocalContext.current

    // Lấy email người dùng đã đăng nhập từ SessionManager (thường được lưu trong SharedPreferences)
    val userLoginEmail = SessionManager(conText).getUserLoginEmail()

    // TODO: Bổ sung xử lý nếu userLoginEmail là null để tránh crash app

    // Lấy thông tin người dùng kiểu khách (guest) từ ViewModel dưới dạng State để tự động cập nhật UI khi dữ liệu thay đổi
    val guest by guestViewModel.selectedGuest.collectAsState()

    // Khi userLoginEmail thay đổi, gọi hàm lấy thông tin guest tương ứng từ ViewModel
    LaunchedEffect(userLoginEmail) {
        userLoginEmail?.let { email ->
            guestViewModel.fetchGuestByEmail(email)
        }
    }

    // Hiển thị icon avatar người dùng (mặc định là hình tròn có người)
    Icon(
        imageVector = Icons.Default.AccountCircle, // Icon avatar mặc định
        contentDescription = "Avatar",
        modifier = Modifier
            .size(32.dp) // Kích thước icon
            .clickable {
                // Khi người dùng bấm vào avatar, xác định sẽ điều hướng đến màn hình nào

                // Nếu là sinh viên (email kết thúc bằng @e.tlu.edu.vn)
                if (userLoginEmail.toString().endsWith("@e.tlu.edu.vn")) {
                    navController.navigate("update_detail_student")
                }

                // Nếu là giảng viên (email @tlu.edu.vn) hoặc khách nhưng có kiểu là "staff"
                else if (userLoginEmail.toString().endsWith("@tlu.edu.vn") || guest?.userType == "staff") {
                    navController.navigate("update_detail")
                }

                // Còn lại là khách
                else {
                    navController.navigate("update_detail_guest")
                }
            }
    )
}

@Composable
fun Staffitem(
    staff: Staff, // Dữ liệu giảng viên cụ thể
    isSelected: Boolean, // Nếu được chọn, hiển thị thêm thông tin
    onClick: () -> Unit, // Callback khi người dùng click vào item
    navController: NavController // Dùng để điều hướng đến màn hình chi tiết
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Chiều rộng tối đa
            .clickable(onClick = onClick) // Bắt sự kiện click
            .padding(horizontal = 16.dp, vertical = 8.dp) // Lề bên trong
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Căn giữa avatar và text theo chiều dọc
        ) {
            // Ảnh đại diện (avatar) hình tròn
            AsyncImage(
                model = staff.avatarURL, // URL ảnh đại diện
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp) // Kích thước avatar
                    .clip(CircleShape) // Cắt ảnh thành hình tròn
                    .background(Color.LightGray, CircleShape) // Nền sáng khi ảnh chưa load
            )

            Spacer(modifier = Modifier.width(12.dp)) // Khoảng cách giữa avatar và văn bản

            Column(
                modifier = Modifier.weight(1f) // Đẩy text chiếm hết chiều rộng còn lại
            ) {
                Text(
                    text = staff.name, // Tên giảng viên
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = staff.position, // Chức vụ giảng viên
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        // Nếu được chọn (đang mở rộng chi tiết)
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đơn vị: ${staff.department}", fontSize = 14.sp)
            Text("Email: ${staff.email}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Gạch phân cách giữa các item
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}


@Composable
fun Stafflist(
    staffs: List<Staff>,
    query: String,
    navController: NavController,
    isFilterActive: Boolean,
    selectedDepartment: String,
    selectedPosition: String,
    staffViewModel: StaffViewModel = viewModel(),
    staffFilterMode: String = "All" // Thêm tham số chế độ lọc
) {
    val groupedByPosition = staffs

        .groupBy { it.position ?: "Không rõ chức vụ" }

    val sortAscending by staffViewModel.sortAscending.collectAsState()

    // Sắp xếp danh sách
    val sortedStaffs = if (sortAscending) {
        staffs.sortedBy { it.name.lowercase() }
    } else {
        staffs.sortedByDescending { it.name.lowercase() }
    }

    // 💡 Lọc theo chế độ được chọn
    val filteredStaffs = sortedStaffs.filter { staff ->
        val matchQuery = staff.name.contains(query, ignoreCase = true)

        val matchDepartment = staff.department.contains(selectedDepartment, ignoreCase = true)

        val matchFilter = when (staffFilterMode) {
            "ByDepartment" -> matchDepartment
            "ByPosition" -> staff.position.contains(selectedPosition, ignoreCase = true)
            else -> true // "All"
        }

        matchQuery && matchFilter
    }

    val letterRange = if (sortAscending) 'A'..'Z' else 'Z' downTo 'A'

    // Nếu đang lọc theo đơn vị hoặc ByDepartment thì nhóm theo đơn vị
    val groupedStaffsByDepartment = filteredStaffs.groupBy { it.department }

    // Ngược lại thì nhóm theo chữ cái đầu tên
    val groupedStaffsByName = letterRange.associateWith { letter ->
        filteredStaffs.filter { it.name.firstOrNull()?.uppercaseChar() == letter }
    }

    LazyColumn {
        if(staffFilterMode == "ByPosition"){
            groupedByPosition.forEach { (positionName, staffList) ->
                item {
                    Text(
                        text = positionName,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
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

        if (staffFilterMode == "ByDepartment") {
            groupedStaffsByDepartment.forEach { (department, staffList) ->
                item {
                    Text(
                        text = department,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
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
        } else {
            groupedStaffsByName.forEach { (letter, staffList) ->
                if (staffList.isNotEmpty()) {
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

@SuppressLint("UnrememberedMutableState") // Bỏ cảnh báo mutableState không được remember đúng cách (dành cho biến fallback)
@Composable
fun Searchbar(
    onStaffFilterChange: (String) -> Unit = {}, // Callback khi người dùng chọn bộ lọc giảng viên
    query: String, // Chuỗi tìm kiếm nhập vào
    onQueryChange: (String) -> Unit, // Callback khi người dùng nhập thay đổi
    selectedTab: String, // Tab hiện tại ("Sinh viên", "Giảng viên", "Đơn vị")
    onFilterClick: () -> Unit, // Callback khi người dùng chọn chức năng lọc
    studentViewModel: StudentViewModel = viewModel(), // ViewModel sinh viên
    staffViewModel: StaffViewModel = viewModel(), // ViewModel giảng viên
    departmentViewModel: DepartmentViewModel? = null, // ViewModel đơn vị (có thể null)
    onDepartmentSortOrderChange: (Boolean) -> Unit = {} // Callback khi đổi thứ tự sắp xếp đơn vị (dự phòng)
) {
    var expanded by remember { mutableStateOf(false) } // Có đang mở menu "More" không?
    var expandedFilter by remember { mutableStateOf(false) } // Có đang mở menu lọc không?
    val dropdownOffset = DpOffset(0.dp, 15.dp) // Đặt vị trí menu chính
    val filterMenuOffset = DpOffset(160.dp, 165.dp) // Đặt vị trí menu lọc (lùi sang phải)

    // Trạng thái sắp xếp hiện tại theo từng ViewModel
    val studentSortAscending by studentViewModel.sortAscending.collectAsState()
    val staffSortAscending by staffViewModel.sortAscending.collectAsState()

    // Trạng thái sắp xếp của department, có thể null -> fallback là true
    val departmentSortAscending by departmentViewModel?.sortAscending?.collectAsState(initial = true)
        ?: remember { mutableStateOf(true) }

    // Lấy đúng sort đang áp dụng theo tab được chọn
    val currentSortAscending = when (selectedTab) {
        "Sinh viên" -> studentSortAscending
        "Giảng viên" -> staffSortAscending
        "Đơn vị" -> departmentSortAscending
        else -> true
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)) // Nền xám nhạt, bo góc
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon tìm kiếm
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(30.dp).padding(start = 3.dp)
            )

            Spacer(Modifier.width(8.dp))

            // Ô nhập text tìm kiếm
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f), // Chiếm toàn bộ chiều rộng còn lại
                singleLine = true
            )

            Spacer(Modifier.width(8.dp))

            // Nút mở menu tuỳ chọn (ba chấm)
            Box {
                // Vị trí của nút mở menu
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Menu xổ xuống (Dropdown) chính
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = dropdownOffset
                ) {

                    DropdownMenuItem(onClick = {
                        // Đảo thứ tự sắp xếp tùy theo tab
                        when (selectedTab) {
                            "Sinh viên" -> studentViewModel.toggleSortOrder()
                            "Giảng viên" -> staffViewModel.toggleSortOrder()
                            "Đơn vị" -> departmentViewModel?.toggleSortOrder()
                        }
                        expanded = false
                    }) {
                        // Hiển thị nội dung theo thứ tự hiện tại
                        Text(
                            if (departmentViewModel?.sortAscending?.collectAsState()?.value == true)
                                "Sắp xếp Z-A"
                            else
                                "Sắp xếp A-Z"
                        )
                    }


                    DropdownMenuItem(onClick = { expandedFilter = true }) {
                        Text("Lọc")
                    }

                    // Menu con: lọc tùy vào tab hiện tại
                    if (expandedFilter) {
                        DropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false },
                            offset = filterMenuOffset
                        ) {
                            when (selectedTab) {
                                "Sinh viên" -> {
                                    DropdownMenuItem(onClick = {
                                        studentViewModel.setFilterMode("ByClass")
                                        expanded = false
                                        expandedFilter = false
                                        onFilterClick()
                                    }) {
                                        Text("Theo Lớp")
                                    }

                                    DropdownMenuItem(onClick = {
                                        studentViewModel.setFilterMode("ByName")
                                        expanded = false
                                        expandedFilter = false
                                        onFilterClick()
                                    }) {
                                        Text("Theo Tên")
                                    }
                                }

                                "Giảng viên" -> {
                                    DropdownMenuItem(onClick = {
                                        onStaffFilterChange("All")
                                        expanded = false
                                        expandedFilter = false
                                    }) {
                                        Text("Tất cả")
                                    }

                                    DropdownMenuItem(onClick = {
                                        onStaffFilterChange("ByDepartment")
                                        expanded = false
                                        expandedFilter = false
                                    }) {
                                        Text("Theo Đơn vị")
                                    }

                                    DropdownMenuItem(onClick = {
                                        onStaffFilterChange("ByPosition")
                                        expanded = false
                                        expandedFilter = false
                                    }) {
                                        Text("Theo Chức vụ")
                                    }
                                }


                                "Đơn vị" -> {
                                    if (departmentViewModel != null) {
                                        DropdownMenuItem(onClick = {
                                            departmentViewModel.setFilterType("")
                                            expandedFilter = false
                                            expanded = false
                                        }) {
                                            Text("Tất cả")
                                        }
                                        DropdownMenuItem(onClick = {
                                            departmentViewModel.setFilterType("Khoa")
                                            expandedFilter = false
                                            expanded = false
                                        }) {
                                            Text("Khoa")
                                        }
                                        DropdownMenuItem(onClick = {
                                            departmentViewModel.setFilterType("Phòng")
                                            expandedFilter = false
                                            expanded = false
                                        }) {
                                            Text("Phòng")
                                        }
                                        DropdownMenuItem(onClick = {
                                            departmentViewModel.setFilterType("Trung tâm")
                                            expandedFilter = false
                                            expanded = false
                                        }) {
                                            Text("Trung tâm")
                                        }
                                        DropdownMenuItem(onClick = {
                                            departmentViewModel.setFilterType("Viện")
                                            expandedFilter = false
                                            expanded = false
                                        }) {
                                            Text("Viện")
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
}


@Composable
fun Bottomnavigationbar(
    selectedTab: String, // Tên của tab hiện tại đang được chọn (ví dụ: "Giảng viên")
    onTabSelected: (String) -> Unit // Hàm callback để xử lý khi người dùng chọn tab khác
) {
    BottomNavigation(
        backgroundColor = Color.White, // Màu nền của thanh điều hướng
        contentColor = Color.Black // Màu mặc định cho nội dung (icon/text)
    ) {

        BottomNavigationItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.department_icon), // Icon từ drawable resource
                    contentDescription = "Đơn vị", // Mô tả cho trình đọc màn hình
                    modifier = Modifier.size(24.dp), // Kích thước icon
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Đơn vị") Color(0xFF007BFE) else Color.Black, // Màu xanh nếu đang chọn, đen nếu không
                        BlendMode.SrcIn
                    )
                )
            },
            label = {
                Text(
                    "Đơn vị", // Nhãn hiển thị bên dưới icon
                    color = if (selectedTab == "Đơn vị") Color(0xFF007BFE) else Color.Black // Tô màu xanh nếu được chọn
                )
            },
            selected = selectedTab == "Đơn vị", // Kiểm tra xem tab này có đang được chọn không
            onClick = { onTabSelected("Đơn vị") } // Khi click, gọi callback để thay đổi tab
        )


        BottomNavigationItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.staff_icon), // Icon từ resource
                    contentDescription = "Giảng viên",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black, // Màu xanh khi được chọn
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
            onClick = { onTabSelected("Giảng viên") } // Gọi callback khi tab được chọn
        )


        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.School, // Icon mặc định từ thư viện material
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
            onClick = { onTabSelected("Sinh viên") } // Chọn tab Sinh viên
        )
    }
}



@Preview(showBackground = true) // Annotation dùng để hiển thị giao diện này trong cửa sổ Preview của Android Studio.
// showBackground = true giúp hiển thị nền trắng, làm cho preview dễ nhìn hơn.
@Composable // Đây là một hàm composable – có thể sử dụng để dựng giao diện trong Jetpack Compose.
fun PreviewScreen() {
    val navController = rememberNavController()
    val staffViewModel = StaffViewModel() // giả lập trong preview
    val studentViewModel = StudentViewModel() // giả lập trong preview
        Directoryscreen(
            navController = navController,
            staffViewModel = staffViewModel,
            studentViewModel = studentViewModel
        )
    }
