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
import androidx.room.*
import kotlinx.coroutines.launch

class home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = ContactDatabase.getDatabase(applicationContext).contactDao()

        lifecycleScope.launch {
            if (dao.getAllStudents().isEmpty()) {
                studentList.values.flatten().forEach { dao.insertStudent(Student(name = it)) }
            }
            if (dao.getAllTeachers().isEmpty()) {
                teacherList.values.flatten().forEach { dao.insertTeacher(Teacher(name = it)) }
            }
            if (dao.getAllDepartments().isEmpty()) {
                departmentList.values.flatten().forEach { dao.insertDepartment(Department(name = it)) }
            }
        }

        setContent {
            DirectoryScreen()
        }
    }
}

// Dữ liệu mẫu
val departmentList = mapOf(
    "K" to listOf("Khoa Cơ khí", "Khoa CNTT", "Khoa Công trình", "Khoa Điện - Điện tử"),
    "P" to listOf("Phòng CT&CTSV", "Phòng Đào tạo", "Phòng Khảo thí", "Phòng Tài chính"),
    "T" to listOf("TT Quốc tế", "TT GDQP", "TT Tin học", "Thư viện"),
    "V" to listOf("Viện TNN", "Viện Công trình", "Viện Thủy lợi")
)

val studentList = mapOf(
    "A" to listOf("Ngô Bá Khá", "Nguyễn Văn A"),
    "B" to listOf("Nguyễn Văn Bình", "Phạm Văn Bờ"),
    "C" to listOf("Nguyễn Chính", "Đỗ Hoài Chung"),
    "D" to listOf("Nguyễn Danh", "Nguyễn Thị Đoàn")
)

val teacherList = mapOf(
    "A" to listOf("Lò Văn A", "Nguyễn An"),
    "B" to listOf("Phạm Văn B", "Lê Văn B"),
    "C" to listOf("Nguyễn Chung", "Đỗ Hoài C"),
    "D" to listOf("Nguyễn D", "Nguyễn Thị D")
)

// ========== UI ==========
@Composable
fun DirectoryScreen() {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }

    var selectedTab by remember { mutableStateOf("Sinh viên") }
    var students by remember { mutableStateOf(emptyList<Student>()) }
    var teachers by remember { mutableStateOf(emptyList<Teacher>()) }
    var departments by remember { mutableStateOf(emptyList<Department>()) }

    LaunchedEffect(true) {
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
            SearchBar()
            Spacer(Modifier.height(16.dp))
            when (selectedTab) {
                "Sinh viên" -> StudentListFromDb(students) {
                    students = dao.getAllStudents()
                }
                "Giảng viên" -> TeacherListFromDb(teachers)
                "Đơn vị" -> DepartmentListFromDb(departments)
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
fun SearchBar() {
    var query by remember { mutableStateOf("") }
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray, CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        BasicTextField(query, { query = it }, Modifier.weight(1f), singleLine = true)
        IconButton(onClick = {}) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.Blue)
        }
    }
}

// ==== Composables danh sách ====
@Composable
fun StudentListFromDb(
    initialStudents: List<Student>,
    onRefreshStudents: suspend () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }
    val scope = rememberCoroutineScope()

    var students by remember { mutableStateOf(initialStudents) }
    var editing by remember { mutableStateOf<Student?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    fun reload() = scope.launch {
        onRefreshStudents()
        students = dao.getAllStudents()
    }

    LazyColumn {
        item {
            Text("Hồ sơ của bạn", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            StudentItem("Ngô Bá Khá")
        }
        students.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { student ->
                StudentItem(student.name) {
                    editing = student
                    newName = student.name
                    showDialog = true
                }
            }
        }
    }

    if (showDialog && editing != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chỉnh sửa sinh viên") },
            text = {
                OutlinedTextField(newName, onValueChange = { newName = it }, label = { Text("Tên mới") })
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        dao.updateStudent(editing!!.copy(name = newName))
                        reload()
                        showDialog = false
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        scope.launch {
                            dao.deleteStudent(editing!!)
                            reload()
                            showDialog = false
                        }
                    }) {
                        Text("Xoá", color = Color.Red)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { showDialog = false }) { Text("Huỷ") }
                }
            }
        )
    }
}

@Composable
fun TeacherListFromDb(teachers: List<Teacher>) {
    LazyColumn {
        item {
            Text("Hồ sơ của bạn", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            TeacherItem("Ngô Bá Khá")
        }
        teachers.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { teacher ->
                TeacherItem(teacher.name)
            }
        }
    }
}

@Composable
fun DepartmentListFromDb(departments: List<Department>) {
    LazyColumn {
        item {
            Text("Hồ sơ của bạn", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            DepartmentItem("Ngô Bá Khá")
        }
        departments.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(initial.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(group) { dept ->
                DepartmentItem(dept.name)
            }
        }
    }
}

// ==== Composable Items ====
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentItem(name: String, onLongClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = {},
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


@Composable
fun TeacherItem(name: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(8.dp))
        Text(name, fontSize = 16.sp)
    }
}

@Composable
fun DepartmentItem(name: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(8.dp))
        Text(name, fontSize = 16.sp)
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

@Preview
@Composable
fun PreviewScreen() {
    DirectoryScreen()
}

// ========== ROOM ENTITIES & DAO ==========

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "units")
data class Department(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
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
