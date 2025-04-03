package com.example.tlucontact.view
import com.example.tlucontact.DetailScreen
import com.example.tlucontact.MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tlucontact.R
import com.example.tlucontact.data.model.Staff
import com.example.tlucontact.data.repository.SessionManager
import com.example.tlucontact.viewmodel.StaffViewModel

class HomeScreen: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "directory"
            ) {
                // Thêm route cho UpdateDetailScreen
                composable(route = "update_detail") {
                    UpdateDetailScreen(
                        onBack = { navController.popBackStack() },  // Quay lại màn hình trước
                        onSave = { /* Xử lý lưu thông tin */ }
                    )
                }

                composable("directory") {
                    Directoryscreen(navController = navController)
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
                        screenTitle = args.getString("screenTitle") ?: "sinh viên",
                        name = args.getString("name") ?: "",
                        studentId = args.getString("studentId") ?: "",
                        className = args.getString("className") ?: "",
                        email = args.getString("email") ?: "",
                        phone = args.getString("phone") ?: "",
                        address = args.getString("address") ?: ""
                    )
                }

                // Route cho Giảng viên
                composable(route = "DetailContactScreen") {
                    val staff = navController.previousBackStackEntry?.savedStateHandle?.get<Staff>("staff")?:Staff("","","","")

                    DetailContactScreen(
                        staff = staff,
                        onBack = { navController.popBackStack() },
                    )
                }

                // Route cho Đơn vị
                composable(
                    route = "department_detail/{name}/{id}/{leader}/{email}/{phone}/{address}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("id") { type = NavType.StringType },
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
                        studentId = args.getString("id") ?: "",
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

// ========== UI ==========
@Composable
fun Directoryscreen(navController: NavController, viewModel: StaffViewModel = StaffViewModel()) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Giảng viên") }
    var query by remember { mutableStateOf("") }
    val staffs by viewModel.staffList.collectAsState()

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
                    sessionManager.clearSession()  // Thêm hàm này để xóa token hoặc trạng thái đăng nhập
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
                Spacer(modifier = Modifier.width(8.dp)) // Khoảng cách giữa avatar và text
                Column {
                    Text("Hồ sơ của bạn", fontSize = 14.sp, color = Color.Gray)
                    Text("Nguyễn Thị Mai Hương", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == "Giảng viên") {
                Stafflist(staffs = staffs, query = query, navController = navController)
            }
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
    val filteredStaffs = staffs.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn {
        items(filteredStaffs) { staff ->
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
@Composable
fun Topbar(
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
fun Searchbar(query: String, onQueryChange: (String) -> Unit) {
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

@Composable
fun Bottomnavigationbar(selectedTab: String, onTabSelected: (String) -> Unit) {
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
                    imageVector = Icons.Default.School, // Biểu tượng Sinh viên
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

