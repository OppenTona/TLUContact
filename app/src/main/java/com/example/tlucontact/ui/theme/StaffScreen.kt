package com.example.tlucontact.ui.theme

import com.example.tlucontact.DetailScreen
import com.example.tlucontact.MainActivity
import com.example.tlucontact.PreferenceHelper
import com.example.tlucontact.readExcelFromUri

import StaffViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import com.example.tlucontact.R
import com.example.tlucontact.data.model.Staff

class StaffScreen: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        navArgument("staffId") { type = NavType.StringType },
                        navArgument("department") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType },
                        navArgument("position") { type = NavType.StringType }
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
fun DirectoryScreen(navController: NavController, viewModel: StaffViewModel = StaffViewModel()) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Giảng viên") }
    var query by remember { mutableStateOf("") }
    val staffs by viewModel.staffList.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab) { newTab ->
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
            TopBar(
                title = "Danh bạ $selectedTab",
                onLogoutClick = {
                    val preferenceHelper = PreferenceHelper(context)
                    preferenceHelper.clearUserData()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(16.dp))
            SearchBar(query = query, onQueryChange = { query = it })
            Spacer(Modifier.height(8.dp))
            UserAvatar()
            Spacer(Modifier.height(16.dp))

            if (selectedTab == "Giảng viên") {
                StaffList(staffs = staffs, query = query, navController = navController)
            }
        }
    }
}

@Composable
fun UserAvatar() {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "Avatar",
        modifier = Modifier.size(32.dp)
    )
}

@Composable
fun StaffItem(staff: Staff, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = staff.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Chức vụ: ${staff.position}", fontSize = 14.sp)
            Text("Đơn vị: ${staff.department}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { /* Gọi điện */ }) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Call")
                }
                IconButton(onClick = { /* Nhắn tin */ }) {
                    Icon(imageVector = Icons.Default.Message, contentDescription = "Message")
                }
                IconButton(onClick = { /* Xem thông tin */ }) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                }
            }
        }
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
fun StaffList(staffs: List<Staff>, query: String, navController: NavController) {
    val filteredStaffs = staffs.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn {
        items(filteredStaffs) { staff ->
            StaffItem(
                staff = staff,
                isSelected = false,
                onClick = {
                    val encodedName = URLEncoder.encode(staff.name, StandardCharsets.UTF_8.toString())
                    val encodedId = URLEncoder.encode(staff.staffId, StandardCharsets.UTF_8.toString())
                    val encodedDepartment = URLEncoder.encode(staff.department, StandardCharsets.UTF_8.toString())
                    val encodedEmail = URLEncoder.encode(staff.email, StandardCharsets.UTF_8.toString())
                    val encodedPhone = URLEncoder.encode(staff.phone, StandardCharsets.UTF_8.toString())
                    val encodedPosition = URLEncoder.encode(staff.position, StandardCharsets.UTF_8.toString())

                    navController.navigate("teacher_detail/$encodedName/$encodedId/$encodedDepartment/$encodedEmail/$encodedPhone/$encodedPosition")
                }
            )
        }
    }
}

@Composable
fun TopBar(
    title: String,
    onLogoutClick: () -> Unit // Thêm tham số onLogoutClick




) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Thay đổi thành SpaceBetween
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onLogoutClick) { // Thêm IconButton đăng xuất
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Đăng xuất"
            )
        }
    }


}


@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownOffset = DpOffset(0.dp, 10.dp) // Điều chỉnh vị trí

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
        BottomNavigationItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.department_icon), // Ảnh Đơn vị
                    contentDescription = "Đơn vị",
                    modifier = Modifier.size(24.dp), // Thu nhỏ icon Đơn vị
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Đơn vị") Color(0xFF007BFE) else Color.Black,
                        BlendMode.SrcIn
                    )
                )
            },
            label = {
                androidx.compose.material.Text(
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
                    painter = painterResource(id = R.drawable.staff_icon), // Ảnh Giảng viên
                    contentDescription = "Giảng viên",
                    modifier = Modifier.size(24.dp), // Thu nhỏ icon Đơn vị
                    colorFilter = ColorFilter.tint(
                        if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black,
                        BlendMode.SrcIn
                    )
                )
            },
            label = {
                androidx.compose.material.Text(
                    "Giảng viên",
                    color = if (selectedTab == "Giảng viên") Color(0xFF007BFE) else Color.Black
                )
            },
            selected = selectedTab == "Giảng viên",
            onClick = { onTabSelected("Giảng viên") }
        )

        BottomNavigationItem(
            icon = {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.School, // Biểu tượng Sinh viên
                    contentDescription = "Sinh viên",
                    tint = if (selectedTab == "Sinh viên") Color(0xFF007BFE) else Color.Black
                )
            },
            label = {
                androidx.compose.material.Text(
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
    DirectoryScreen(navController = navController)
}







