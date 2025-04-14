package com.example.tlucontact.view

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.rememberImagePainter
import com.example.tlucontact.R
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.model.Staff
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.data.repository.DepartmentRepository
import com.example.tlucontact.data.repository.SessionManager
import com.example.tlucontact.data.repository.TempImageStorage
import com.example.tlucontact.viewmodel.DepartmentViewModel
import com.example.tlucontact.viewmodel.GuestViewModel
import com.example.tlucontact.viewmodel.LogOutViewModel
import com.example.tlucontact.viewmodel.StaffViewModel
import com.example.tlucontact.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.StateFlow
import java.text.Collator
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    navControllerLogout: NavController,
) {
    // Tạo đối tượng NavController dùng để điều hướng giữa các màn hình trong Compose
    // rememberNavController sẽ nhớ lại NavController khi giao diện được recomposed
    val navController = rememberNavController()

    // ViewModel dùng chung

    val departmentViewModel: DepartmentViewModel = viewModel() // Tạo hoặc lấy ViewModel có kiểu StudentViewModel, ViewModel này được dùng để quản lý dữ liệu và logic liên quan đến đơn vị
    val staffViewModel: StaffViewModel = viewModel()    // Tạo hoặc lấy ViewModel có kiểu StaffViewModel, ViewModel này được dùng để quản lý dữ liệu và logic liên quan đến giảng viên, viewModel() sẽ tự động gán theo vòng đời của composable
    val studentViewModel: StudentViewModel = viewModel() // Tạo hoặc lấy ViewModel có kiểu StudentViewModel, ViewModel này được dùng để quản lý dữ liệu và logic liên quan đến sinh viên
    val guestViewModel: GuestViewModel = viewModel()
    val logOutViewModel: LogOutViewModel = viewModel() // Sử dụng ViewModel
    val logOutState by logOutViewModel.logoutState.collectAsState() // Theo dõi trạng thái đăng xuất

    val selectedStaff by staffViewModel.selectedStaff.collectAsState()
    val selectedStudent by studentViewModel.selectedStudent.collectAsState() // Lấy thông tin sinh viên đang được chọn từ ViewModel
    val selectedGuest by guestViewModel.selectedGuest.collectAsState()
    // LaunchedEffect sẽ chạy khối code bên trong khi giá trị logoutState thay đổi
    LaunchedEffect(logOutState) {
        // Nếu logoutState.first == true → đăng xuất thành công
        if (logOutState.first) {
            // Điều hướng sang màn hình đăng nhập (login)
            navControllerLogout.navigate("login") {
                // Xóa toàn bộ backstack (xóa hết các màn hình trước đó)
                popUpTo(0) { inclusive = true }
            }
        }
        // Nếu logoutState.second khác null → có lỗi xảy ra khi đăng xuất
        else if (logOutState.second != null) {
            // Hiển thị thông báo lỗi bằng Toast
            Toast.makeText(
                navController.context,
                "Lỗi: ${logOutState.second}",
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
                logOutViewModel = logOutViewModel
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
                    address = args.getString("address") ?: "",
                    // Không lấy photoURL từ arguments nữa
                    photoURL = ""  // Sẽ được ghi đè trong DetailStudentScreen
                ),
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

            DepartmentDetailView(
                name = Uri.decode(args.getString("name") ?: ""),
                id = Uri.decode(args.getString("id") ?: ""),
                leader = Uri.decode(args.getString("leader") ?: ""),
                email = Uri.decode(args.getString("email") ?: ""),
                phone = Uri.decode(args.getString("phone") ?: ""),
                address = Uri.decode(args.getString("address") ?: ""),
                onBack = { navController.popBackStack() }
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
    logOutViewModel: LogOutViewModel = viewModel(),
    departmentViewModel: DepartmentViewModel = viewModel()
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

    // Lấy dữ liệu từ các ViewModel
    val department by departmentViewModel.departmentList.collectAsState()

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
    val guest by guestViewModel.selectedGuest.collectAsState()


    LaunchedEffect(userLoginEmail) {
        if (!userLoginEmail.isNullOrBlank()) {
            when {
                userLoginEmail.endsWith("@e.tlu.edu.vn") -> {
                    Log.d("Navigation", "Navigating to update_detail_student")
                    studentViewModel.setStudentByEmail(userLoginEmail)
                    studentViewModel.fetchStudents(userLoginEmail)
                }
                userLoginEmail.endsWith("@tlu.edu.vn") || guest?.userId == "staff" -> {
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
                onLogoutClick = { logOutViewModel.logout() } // Gọi logout khi nhấn nút
            )

            Spacer(Modifier.height(16.dp)) // Khoảng cách

            // Thanh tìm kiếm, có thể hiển thị thêm lọc nếu là Đơn vị
            Searchbar(
                query = query, // Nội dung tìm kiếm hiện tại
                onQueryChange = { query = it }, // Cập nhật query khi người dùng nhập
                selectedTab = selectedTab, // Biết đang ở tab nào để xử lý đúng
                onFilterClick = { isFilterActive = true }, // Mở lọc khi nhấn icon lọc
                //departmentViewModel = departmentViewModel, // Truyền ViewModel đơn vị
                departmentViewModel = if (selectedTab == "Đơn vị") departmentViewModel else null,
            )

            Spacer(Modifier.height(8.dp))

            // Hiển thị avatar người dùng và tên hồ sơ hiện tại (giảng viên, sinh viên, hoặc khách)
            Row(
                verticalAlignment = Alignment.CenterVertically, // canh giữa theo chiều dọc của phần tử cha
                modifier = Modifier.padding(8.dp)
            ) {
                Useravatar(navController, guestViewModel) // Hiển thị avatar

                Spacer(modifier = Modifier.width(8.dp))

                Column { //Hiển thị theo cột
                    Text("Hồ sơ của bạn", fontSize = 14.sp, color = Color.Gray) // văn bản Hồ sơ của bạn

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
                "Giảng viên" -> Stafflist( // nếu tab được chọn là giảng viên thì hiển thị màn hình tương ứng là Stafflist
                    staffs = staffs,
                    query = query,
                    navController = navController,
                    // staffViewModel = staffViewModel,

                )

                "Đơn vị" -> DepartmentList(
                    query = query,
                    navController = navController,
                    departmentViewModel = departmentViewModel,
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
        filteredStudents.sortedBy { extractLastNameForSort(it.fullNameStudent) }
    } else {
        filteredStudents.sortedByDescending { extractLastNameForSort(it.fullNameStudent) }
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
                extractFirstLetterOfLastName(it.fullNameStudent)
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
            .clickable {
                // Lưu URL vào bộ nhớ tạm trước khi điều hướng
                TempImageStorage.setImageUrl(student.photoURL)

                // Điều hướng như bình thường, không cần truyền photoURL
                navController.navigate(
                    "student_detail/" +
                            "${Uri.encode(student.fullNameStudent)}/" +
                            "${student.studentID}/" +
                            "${Uri.encode(student.className)}/" +
                            "${Uri.encode(student.email)}/" +
                            "${Uri.encode(student.phone)}/" +
                            "${Uri.encode(student.address)}"
                )
                onClick()
            }
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
    query: String,
    navController: NavController,
    departmentViewModel: DepartmentViewModel = viewModel()
) {
    val sortAscending by departmentViewModel.sortAscending.collectAsState()
    val filterMode by departmentViewModel.filterMode.collectAsState()
    val filteredDepartments by departmentViewModel.filteredDepartmentList.collectAsState()

    Column {
        if (filterMode == "Tất cả") {
            // Hiển thị theo bảng chữ cái
            val sortedDepartments = if (sortAscending) {
                filteredDepartments.sortedBy { it.name.lowercase() }
            } else {
                filteredDepartments.sortedByDescending { it.name.lowercase() }
            }

            val groupedDepartments = sortedDepartments.groupBy {
                it.name.trim().firstOrNull()?.uppercaseChar() ?: '#'
            }

            LazyColumn {
                groupedDepartments.forEach { (letter, departmentList) ->
                    if (letter != '#') {
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
                    items(departmentList) { department ->
                        DepartmentItem(
                            department = department,
                            isSelected = false,
                            onClick = {
                                navController.navigate(
                                    "department_detail/" +
                                            "${Uri.encode(department.name)}/" +
                                            "${Uri.encode(department.id)}/" +
                                            "${Uri.encode(department.leader)}/" +
                                            "${Uri.encode(department.email)}/" +
                                            "${Uri.encode(department.phone)}/" +
                                            "${Uri.encode(department.address)}?screenTitle=${Uri.encode(department.name)}"
                                )
                            },
                            navController = navController
                        )
                    }
                }
            }
        } else {
            // Hiển thị theo loại đơn vị
            val groupedByType = filteredDepartments.groupBy {
                it.type
            }

            LazyColumn {
                groupedByType.forEach { (type, departmentList) ->
                    item {
                        Text(
                            text = type.ifEmpty { "Khác" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 16.dp)
                        )
                    }

                    items(departmentList) { department ->
                        DepartmentItem(
                            department = department,
                            isSelected = false,
                            onClick = {
                                navController.navigate(
                                    "department_detail/" +
                                            "${Uri.encode(department.name)}/" +
                                            "${Uri.encode(department.id)}/" +
                                            "${Uri.encode(department.leader)}/" +
                                            "${Uri.encode(department.email)}/" +
                                            "${Uri.encode(department.phone)}/" +
                                            "${Uri.encode(department.address)}?screenTitle=${Uri.encode(department.name)}"
                                )
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
fun DepartmentItem(
    department: Department,
    isSelected: Boolean,
    onClick: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically // Sắp xếp các thành phần bên trong theo chiều ngang, căn giữa theo chiều dọc
        ) {
            // Hiển thị ảnh đại diện của đơn vị
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

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                // Hiển thị tên đơn vị
                Text(text = department.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                // Thêm khoảng cách 4dp giữa Text và Divider
                Spacer(modifier = Modifier.height(4.dp))

                // Kẻ đường gạch chân dưới tên đơn vị
                Divider(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.wrapContentWidth(Alignment.Start) // Giới hạn chiều rộng theo nội dung và căn trái
                )
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Mã đơn vị: ${department.id}", fontSize = 14.sp)
            Text("Trưởng đơn vị: ${department.leader}", fontSize = 14.sp)
            Text("Email: ${department.email}", fontSize = 14.sp)
            Text("Số điện thoại: ${department.phone}", fontSize = 14.sp)
            Text("Địa chỉ: ${department.address}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Useravatar(navController: NavController, guestViewModel: GuestViewModel) {
    val context = LocalContext.current                            // Lấy context hiện tại của Composable
    val userLoginEmail = SessionManager(context).getUserLoginEmail() // Lấy email người dùng đã đăng nhập từ SessionManager
    val guest by guestViewModel.selectedGuest.collectAsState()    // Lấy dữ liệu người dùng (Guest) từ ViewModel bằng State để UI tự động cập nhật khi có thay đổi

    // Gọi hàm lấy thông tin người dùng từ Firestore nếu có email
    LaunchedEffect(userLoginEmail) {
        userLoginEmail?.let { email ->
            guestViewModel.fetchGuestByEmail(email)              // Gọi hàm trong ViewModel để lấy thông tin người dùng theo email
        }
    }

    val avatarUrl = guest?.avatarURL                             // Lưu đường dẫn ảnh avatar nếu có

    // UI hiển thị avatar người dùng, hoặc icon mặc định nếu không có ảnh
    Box(
        modifier = Modifier
            .size(35.dp)                                         // Kích thước khung avatar
            .clip(CircleShape)                                   // Bo tròn khung thành hình tròn
            .clickable {
                // Khi người dùng click vào avatar, điều hướng đến màn hình tương ứng
                if (userLoginEmail?.endsWith("@e.tlu.edu.vn") == true) {
                    navController.navigate("update_detail_student")   // Nếu là sinh viên
                } else if (userLoginEmail?.endsWith("@tlu.edu.vn") == true || guest?.userType == "staff") {
                    navController.navigate("update_detail")           // Nếu là cán bộ/giảng viên
                } else {
                    navController.navigate("update_detail_guest")     // Nếu là khách
                }
            }
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            // Nếu có avatarURL, hiển thị ảnh
            Image(
                painter = rememberImagePainter(avatarUrl),       // Dùng Coil để tải ảnh từ URL
                contentDescription = "Avatar",                   // Mô tả cho accessibility
                contentScale = ContentScale.Crop,                // Cắt ảnh để vừa khung tròn
                modifier = Modifier
                    .fillMaxSize()                               // Ảnh lấp đầy khung
                    .border(1.dp, Color.Gray, CircleShape)       // Viền xám mỏng quanh avatar
            )
        } else {
            // Nếu không có avatar, hiển thị icon mặc định
            Icon(
                imageVector = Icons.Default.AccountCircle,       // Icon tài khoản mặc định
                contentDescription = "Default Avatar",           // Mô tả cho accessibility
                modifier = Modifier
                    .fillMaxSize()                               // Icon lấp đầy khung
                    .border(1.dp, Color.Gray, CircleShape)       // Viền xám mỏng
            )
        }
    }
}


@Composable
fun Staffitem(
    staff: Staff, // Dữ liệu giảng viên cụ thể
    isSelected: Boolean, // Nếu được chọn, hiển thị thêm thông tin
    onClick: () -> Unit, // Callback khi người dùng click vào item

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
    staffs: List<Staff>, // Danh sách tất cả giảng viên
    query: String, // Chuỗi tìm kiếm (tên giảng viên)
    navController: NavController, // Dùng để điều hướng đến màn hình chi tiết
    staffViewModel: StaffViewModel = viewModel() // ViewModel quản lý danh sách và trạng thái lọc/sắp xếp
) {
    // Lấy trạng thái sắp xếp từ ViewModel (tăng hay giảm dần theo tên)
    val sortAscending by staffViewModel.sortAscending.collectAsState()
    val filterMode by staffViewModel.filterMode.collectAsState() // Lấy chế độ lọc từ ViewModel

    val collator = Collator.getInstance(Locale("vi", "VN"))
    collator.strength = Collator.PRIMARY // Bỏ qua phân biệt hoa thường và dấu

    // Sắp xếp danh sách giảng viên theo tên (tăng/giảm dần)
    val sortedStaffs = if (sortAscending) { // sắp xếp theo collator để có chữ tiếng việt
        staffs.sortedWith(compareBy(collator) { it.name }) //sx giảm đần
    } else {
        staffs.sortedWith(compareByDescending(collator) { it.name }) // sắp xếp tăng dần
    }

    // Lọc danh sách giảng viên theo tên và filterMode
    //Duyệt qua từng phần từ trong danh sách sortstaffs và chỉ giữ lại các phần tử thỏa mãn điều kiện
    val filteredStaffs = sortedStaffs.filter { // khai báo biến chứa danh sashc đã lọc
        // kiểm tra xem tên của staff có chứa chuỗi query không (không phân biệt chữ hoa và thường)
        it.name.contains(query, ignoreCase = true) //
    }

    // Nhóm danh sách theo chế độ lọc
    val groupedStaffs = when (filterMode) {
        // khai báo 1 biến và gán cho nó kết quả phân nhóm (groupBy) danh sách filteredStaffs theo tiêu chí được chọn trong filterMode
        "ByAll" -> filteredStaffs.groupBy { it.name.firstOrNull()?.uppercaseChar() }
        "ByDepartment" -> filteredStaffs.groupBy { it.department }
        "ByPosition" -> filteredStaffs.groupBy { it.position } // it đại diện cho từng staff, name là tên của staff,
        // firstOrNUll lấy kí tự đầu tiên trong tên, nếu rỗng trả về null, nếu có ký tự đầu tiên, thì chuyển nó thành chữ in hoa.

        else -> filteredStaffs.groupBy { it.name.firstOrNull()?.uppercaseChar() } // Nếu key không khớp với key hiện có
    }

    // Hiển thị danh sách dạng LazyColumn (cuộn được)
    LazyColumn {
        groupedStaffs.forEach { (key, staffList) -> //Nhóm staff theo key là chức vụ hoặc đơn vị trong danh sách staff
            item { // Đây là tên của group (ví dụ: Khoa công nghệ thông tin hay Giảng viên)
                Text(
                    text = key.toString(), // Hiển thị tên nhóm (bộ môn hoặc chức vụ)
                    fontSize = 16.sp, // cỡ chữ 16dp
                    fontWeight = FontWeight.Medium, //độ đậm của chữ
                    color = Color.Gray, // màu chữ xám
                    modifier = Modifier //điều chỉnh
                        .fillMaxWidth() // giãn hết cỡ
                        .padding(horizontal = 12.dp, vertical = 4.dp) ///đệm theo chiều ngang 12dp, chiều dọc 4dp
                )
            }

            // Hiển thị giảng viên thuộc nhóm đó
            items(staffList) { staff -> // duyệt qua danh sách staff, với mỗi staff thì vẽ 1 Staffitem như dưới
                Staffitem( // toàn một một item dạng staff trong danh sách
                    staff = staff, // gán là đối tượng staff
                    isSelected = false, //gán là chưa chọn
                    onClick = {
                        //currentBackStackEntry là Entry (màn hình) hiện tại đang ở trên top của back stack (ngăn xếp điều hướng).
                        //savedStateHandle  Một nơi để lưu trữ tạm trạng thái hoặc dữ liệu liên quan đến entry đó, có thể chia sẻ giữa các màn hình
                        navController.currentBackStackEntry?.savedStateHandle?.set("staff", staff) // lưu giữ liệu với key là staff value là đối tượng staff
                        navController.navigate("DetailContactScreen") // điều hướng đến giao diện thông tin chi tiết
                    },
                )
            }
        }
    }
}


@Composable
fun Topbar( // Thanh tiêu đề trên cùng
    title: String,                    // Tiêu đề sẽ hiển thị trên thanh topbar
    onLogoutClick: () -> Unit        // Hàm callback được gọi khi nhấn nút đăng xuất
) {
    Row(                              // Dùng Row để sắp xếp title và nút logout trên cùng một hàng ngang
        Modifier.fillMaxWidth(),      // Chiếm toàn bộ chiều ngang
        horizontalArrangement = Arrangement.SpaceBetween, // Các phần tử được dàn đều hai bên
        verticalAlignment = Alignment.CenterVertically     // Canh giữa theo chiều dọc
    ) {
        Text(                         // Hiển thị tiêu đề
            title,
            fontSize = 20.sp,         // Cỡ chữ
            fontWeight = FontWeight.Bold // In đậm
        )
        IconButton(onClick = onLogoutClick) { // Nút đăng xuất, gọi hàm onLogoutClick khi được nhấn
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout, // Icon logout tự xoay theo chiều giao diện (LTR/RTL)
                contentDescription = "Đăng xuất"                // Mô tả cho accessibility
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
    //onDepartmentSortOrderChange: (Boolean) -> Unit = {} // Callback khi đổi thứ tự sắp xếp đơn vị (dự phòng)
) {
    var expanded by remember { mutableStateOf(false) } // Có đang mở menu "More" không?
    var expandedFilter by remember { mutableStateOf(false) } // Có đang mở menu lọc không?
    val dropdownOffset = DpOffset(0.dp, 15.dp) // Đặt vị trí menu chính
    val filterMenuOffset = DpOffset(160.dp, 165.dp) // Đặt vị trí menu lọc (lùi sang phải)

    // Trạng thái sắp xếp hiện tại theo từng ViewModel
    val studentSortAscending by studentViewModel.sortAscending.collectAsState()
    // Khai báo một biến staffSortAscending có giá trị được lấy từ viewmodel
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

    Box( // khung hình chữ nhật
        modifier = Modifier // căn chỉnh
            .fillMaxWidth() // giãn hết cỡ
            .height(36.dp) // chiều cao 36dp
            .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)) // Nền xám nhạt, bo góc
            .padding(horizontal = 8.dp), // đệm bên trong theo trục hoành
        contentAlignment = Alignment.CenterStart // Canh giữa theo chiều dọc, canh trái theo chiều ngang
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // giãn hết cỡ
            verticalAlignment = Alignment.CenterVertically // Căn giữa theo chiều dọc so với chiều cao của row
        ) {
            // Icon tìm kiếm
            Icon(
                Icons.Default.Search, // icon tìm kiếm
                contentDescription = null, // không có tiêu đề
                modifier = Modifier.size(30.dp).padding(start = 3.dp) // chỉnh sửa cỡ 30dp và độn bên trong 3dp
            )

            Spacer(Modifier.width(8.dp)) // Khoảng cách giữa icon và ô nhập text

            // Ô nhập text tìm kiếm
            BasicTextField( // BasicTextField cho phép tùy biến nhiều hơn
                value = query, // Nội dung ô tìm kiếm
                onValueChange = {
                    onQueryChange(it)
                    if (selectedTab == "Đơn vị") {
                        departmentViewModel?.setQuery(it) // Gọi hàm setQuery khi query thay đổi
                    }
                },
                //onValueChange = onQueryChange, // Cập nhật nội dung khi người dùng nhập
                modifier = Modifier.weight(1f), // Chiếm toàn bộ chiều rộng còn lại
                singleLine = true // Chỉ cho phép nhập một dòng
            )

            Spacer(Modifier.width(8.dp)) // khoảng cách giữa ô tìm kiếm và nút mở menu

            // Nút mở menu tuỳ chọn (ba chấm)
            Box {
                // Vị trí của nút mở menu
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.MoreVert, // Icon ba chấm
                        contentDescription = "More Options", // mô tả content
                        modifier = Modifier.size(30.dp) // Chỉnh kích cỡ 30dp
                    )
                }

                // Menu xổ xuống (Dropdown) chính
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = dropdownOffset
                ) {
                    // Đảo thứ tự sắp xếp tùy theo tab
                    DropdownMenuItem(onClick = {
                        when (selectedTab) {
                            "Sinh viên" -> studentViewModel.toggleSortOrder()
                            "Giảng viên" -> staffViewModel.toggleSortOrder() // Đảo thứ tự giảng viên
                            "Đơn vị" -> departmentViewModel?.toggleSortOrder()
                        }
                        expanded = false // Đóng menu sau khi chọn
                    }) {
                        val label = when (selectedTab) { // Xác định nhãn cho nút sắp xếp
                            "Sinh viên" -> if (studentSortAscending) "Sắp xếp Z-A" else "Sắp xếp A-Z" // Sắp xếp sinh viên
                            "Giảng viên" -> if (staffSortAscending) "Sắp xếp Z-A" else "Sắp xếp A-Z"  // Sắp xếp giảng viên
                            "Đơn vị" -> if (departmentSortAscending) "Sắp xếp Z-A" else "Sắp xếp A-Z" // Sắp xếp đơn vị
                            else -> "Sắp xếp" // Giá trị mặc định nếu không có tab nào được chọn
                        }
                        Text(text = label) // Hiển thị chữ "Sắp xếp" với thứ tự tương ứng
                    }

                    // Nút mở menu lọc
                    DropdownMenuItem(onClick = { expandedFilter = true }) {
                        Text("Lọc") // Hiển thị chữ "Lọc" trên menu
                    }

                    // Menu con: lọc tùy vào tab hiện tại
                    if (expandedFilter) {
                        DropdownMenu(
                            expanded = expandedFilter, // Mở menu lọc
                            onDismissRequest = { expandedFilter = false }, // Đóng menu khi nhấn ra ngoài
                            offset = filterMenuOffset // Vị trí menu lọc
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

                                "Giảng viên" -> { // Lọc theo giảng viên
                                    DropdownMenuItem(onClick = {
                                        staffViewModel.setFilterMode("ByAll") // Lọc theo tất cả
                                        expanded = false
                                        expandedFilter = false
                                    }) {
                                        Text("Tất cả") // Tiêu đề lọc theo all
                                    }
                                    DropdownMenuItem(onClick = {
                                        staffViewModel.setFilterMode("ByDepartment") // Lọc theo đơn vị
                                        expanded = false
                                        expandedFilter = false
                                    }) {
                                        Text("Theo Đơn vị") // Tiêu đề lọc theo đơn vị
                                    }
                                    DropdownMenuItem(onClick = { // Lọc theo chức vụ
                                        staffViewModel.setFilterMode("ByPosition") // Lọc theo chức vụ
                                        expanded = false // Đóng menu
                                        expandedFilter = false // Đóng menu
                                    }) {
                                        Text("Theo Chức vụ") // Tiêu đề lọc theo chức vụ
                                    }
                                }
                                "Đơn vị" -> {
                                    DropdownMenuItem(onClick = {
                                        departmentViewModel?.setFilterMode("Tất cả")
                                        departmentViewModel?.applyFilters() // Áp dụng bộ lọc
                                        expandedFilter = false
                                        expanded = false
                                        onFilterClick()
                                    }) {
                                        Text("Tất cả")
                                    }

                                    DropdownMenuItem(onClick = {
                                        departmentViewModel?.setFilterMode("Khoa")
                                        departmentViewModel?.applyFilters() // Áp dụng bộ lọc
                                        expandedFilter = false
                                        expanded = false
                                        onFilterClick()
                                    }) {
                                        Text("Khoa")
                                    }

                                    DropdownMenuItem(onClick = {
                                        departmentViewModel?.setFilterMode("Phòng")
                                        departmentViewModel?.applyFilters() // Áp dụng bộ lọc
                                        expandedFilter = false
                                        expanded = false
                                        onFilterClick()
                                    }) {
                                        Text("Phòng")
                                    }

                                    DropdownMenuItem(onClick = {
                                        departmentViewModel?.setFilterMode("Trung tâm")
                                        departmentViewModel?.applyFilters() // Áp dụng bộ lọc
                                        expandedFilter = false
                                        expanded = false
                                        onFilterClick()
                                    }) {
                                        Text("Trung tâm")
                                    }

                                    DropdownMenuItem(onClick = {
                                        departmentViewModel?.setFilterMode("Viện")
                                        departmentViewModel?.applyFilters() // Áp dụng bộ lọc
                                        expandedFilter = false
                                        expanded = false
                                        onFilterClick()
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

@Composable
// Hàm Bottomnavigationbar để tạo thanh điều hướng dưới cùng
fun Bottomnavigationbar(
    selectedTab: String, // Tên của tab hiện tại đang được chọn (ví dụ: "Giảng viên")
    onTabSelected: (String) -> Unit // Hàm callback để xử lý khi người dùng chọn tab khác
) {
    BottomNavigation( // Thanh điều hướng dưới cùng
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


        BottomNavigationItem( // Tab giảng viên
            icon = {
                Image( // Sử dụng Image để hiển thị icon
                    painter = painterResource(id = R.drawable.staff_icon), // Icon từ resource
                    contentDescription = "Giảng viên",
                    modifier = Modifier.size(24.dp), // Kích thước icon là 24dp
                    colorFilter = ColorFilter.tint( // Tô màu icon, ColorFilter.tint giúp tô màu icon theo màu đã chỉ định
                        if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black, // Màu xanh khi được chọn
                        BlendMode.SrcIn // Tô màu icon, BlendMode.SrcIn giúp tô màu icon theo màu đã chỉ định
                    )
                )
            },
            label = { // Nhãn hiển thị bên dưới icon
                Text(
                    "Giảng viên", // Nhãn hiển thị bên dưới icon
                    color = if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black // Tô màu xanh nếu được chọn nếu không chọn thì là màu đen
                )
            },
            selected = selectedTab == "Giảng viên", // Kiểm tra xem tab này có đang được chọn không
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

// Hàm lấy chữ cái đầu của tên riêng
fun extractFirstLetterOfLastName(fullName: String): Char {
    return fullName.trim()
        .split(" ")
        .lastOrNull()
        ?.firstOrNull()
        ?.uppercaseChar() ?: '#'
}

// Hàm dùng để sắp xếp theo tên riêng
fun extractLastNameForSort(fullName: String): String {
    return fullName.trim()
        .split(" ")
        .lastOrNull()
        ?.lowercase() ?: ""
}



@Preview(showBackground = true) // Annotation dùng để hiển thị giao diện này trong cửa sổ Preview của Android Studio.
// showBackground = true giúp hiển thị nền trắng, làm cho preview dễ nhìn hơn.
@Composable // Đây là một hàm composable – có thể sử dụng để dựng giao diện trong Jetpack Compose.
// Đây là hàm Preview dùng để kiểm tra giao diện mà không cần chạy ứng dụng thực tế.
fun PreviewScreen() {
    val navController = rememberNavController() // Tạo một NavController giả lập để sử dụng trong preview
    val staffViewModel = StaffViewModel() // giả lập trong preview
    val studentViewModel = StudentViewModel() // giả lập trong preview
    Directoryscreen( // Gọi hàm Directoryscreen để hiển thị giao diện
        navController = navController, // Truyền vào NavController
        staffViewModel = staffViewModel, // Giả lập ViewModel giảng viên
        studentViewModel = studentViewModel // Giả lập ViewModel sinh viên
    )

}