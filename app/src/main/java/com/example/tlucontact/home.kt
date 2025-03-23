package com.example.tlucontact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch

class home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = ContactDatabase.getDatabase(applicationContext).contactDao()

        // Khởi tạo dữ liệu mẫu chỉ khi database còn trống
        lifecycleScope.launch {
            if (dao.getAllStudents().isEmpty()) {
                studentList.values.flatten().forEach {
                    dao.insertStudent(Student(name = it))
                }
            }

            if (dao.getAllTeachers().isEmpty()) {
                teacherList.values.flatten().forEach {
                    dao.insertTeacher(Teacher(name = it))
                }
            }

            if (dao.getAllUnits().isEmpty()) {
                unitList.values.flatten().forEach {
                    dao.insertUnit(Unit(name = it))
                }
            }
        }

        setContent {
            DirectoryScreen()
        }
    }
}

val unitList = mapOf(
    "K" to listOf("Khoa Cơ khí", "Khoa Công nghệ thông tin", "Khoa Công trình", "Khoa Điện - Điện tử"),
    "P" to listOf("Phòng Chính trị và Công tác sinh viên", "Phòng Đào tạo", "Phòng Khảo thí và Đảm bảo chất lượng", "Phòng Tài chính - Kế toán"),
    "T" to listOf("Trung tâm Đào tạo quốc tế", "Trung tâm Giáo dục Quốc phòng và An ninh", "Trung tâm Tin học", "Thư viện"),
    "V" to listOf("Viện Kỹ thuật tài nguyên nước", "Viện Kỹ thuật công trình", "Viện Thủy lợi và Môi trường")
)

// Dữ liệu danh bạ sinh viên nhóm theo ký tự đầu
val studentList = mapOf(
    "A" to listOf("Ngô Bá Khá", "Nguyễn Văn A", "Nguyễn Thị An", "Phạm Thị Anh"),
    "B" to listOf("Nguyễn Văn Bình", "Nguyễn Thị Bình", "Phạm Thị Đức Bơ", "Phạm Văn Bờ"),
    "C" to listOf("Nguyễn Chính", "Nguyễn Chiến", "Vũ Văn Chương", "Đỗ Hoài Chung"),
    "D" to listOf("Nguyễn Văn Danh", "Nguyễn Thị Đoàn")
)

// Dữ liệu danh bạ giảng viên nhóm theo ký tự đầu
val teacherList = mapOf(
    "A" to listOf("Lò Văn A", "Nguyễn An", "Lê Thị A"),
    "B" to listOf("Phạm Văn B", "Nguyễn Thị B", "Phạm Thị B", "Lê Văn B"),
    "C" to listOf("Nguyễn Chung", "Lê Văn C", "Vũ Văn C", "Đỗ Hoài C"),
    "D" to listOf("Nguyễn D", "Nguyễn Thị D")
)

// Màn hình chính: Hiển thị danh bạ sinh viên hoặc giảng viên
@Composable
fun DirectoryScreen() {
    val context = LocalContext.current
    val dao = remember { ContactDatabase.getDatabase(context).contactDao() }

    var selectedTab by remember { mutableStateOf("Sinh viên") }

    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var teachers by remember { mutableStateOf<List<Teacher>>(emptyList()) }
    var units by remember { mutableStateOf<List<Unit>>(emptyList()) }

    // Lấy dữ liệu từ database khi Composable được tạo
    LaunchedEffect(true) {
        students = dao.getAllStudents()
        teachers = dao.getAllTeachers()
        units = dao.getAllUnits()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedTab) { newTab -> selectedTab = newTab }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TopBar(title = "Danh bạ $selectedTab")
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                "Sinh viên" -> StudentListFromDb(students)
                "Giảng viên" -> TeacherListFromDb(teachers)
                "Đơn vị" -> UnitListFromDb(units)
            }
        }
    }
}


// Thanh tiêu đề trên cùng
@Composable
fun TopBar(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier.size(32.dp)
        )
    }
}

// Thanh tìm kiếm
@Composable
fun SearchBar() {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        IconButton(onClick = { /* Xử lý bộ lọc */ }) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter Icon", tint = Color.Blue)
        }
    }
}

// Danh sách sinh viên
@Composable
fun StudentList(studentMap: Map<String, List<String>>, context: Context) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp).clickable {
                    //val intent = Intent(context, EditProfileActivity::class.java)
                    //context.startActivity(intent)
                }
            )
            StudentItem("Ngô Bá Khá")
        }
        studentMap.forEach { (letter, students) ->
            item {
                Text(
                    text = letter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            }
            items(students) { student ->
                StudentItem(student)
            }
        }
    }
}

// Danh sách giảng viên
@Composable
fun TeacherList(teacherMap: Map<String, List<String>>, context: Context) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            TeacherItem("Ngô Bá Khá")
        }
        teacherMap.forEach { (letter, teachers) ->
            item {
                Text(
                    text = letter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        //val intent = Intent(context, EditProfileActivity::class.java)
                        //context.startActivity(intent)
                    }
                )
            }
            items(teachers) { teacher ->
                TeacherItem(teacher)
            }
        }
    }
}

// Danh sách don vi
@Composable
fun UnitList(unitMap: Map<String, List<String>>, context: Context) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            UnitItem("Ngô Bá Khá")
            //val intent = Intent(context, EditProfileActivity::class.java)
            //context.startActivity(intent)
        }
        unitMap.forEach { (letter, units) ->
            item {
                Text(
                    text = letter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        //val intent = Intent(context, EditProfileActivity::class.java)
                        //context.startActivity(intent)
                    }
                )
            }
            items(units) { unit ->
                TeacherItem(unit)
            }
        }
    }
}

// Item danh sách sinh viên
@Composable
fun StudentItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp)
    }
}

// Item danh sách giảng viên
@Composable
fun TeacherItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp)
    }
}


@Composable
fun UnitItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            tint = Color.LightGray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, fontSize = 16.sp)
    }
}

// Thanh điều hướng (Bottom Navigation)
@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> kotlin.Unit) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        BottomNavigationItem(
            icon = { Text("🏢") },
            label = { Text("Đơn vị") },
            selected = selectedTab == "Đơn vị",
            onClick = { onTabSelected("Đơn vị") }
        )
        BottomNavigationItem(
            icon = { Text("👨‍🏫") },
            label = { Text("Giảng viên") },
            selected = selectedTab == "Giảng viên",
            onClick = { onTabSelected("Giảng viên") }
        )
        BottomNavigationItem(
            icon = { Text("🎓") },
            label = { Text("Sinh viên") },
            selected = selectedTab == "Sinh viên",
            onClick = { onTabSelected("Sinh viên") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentDirectoryScreen() {
    DirectoryScreen()
}


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
data class Unit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)


@Dao
interface ContactDao {
    // Student
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getAllStudents(): List<Student>

    // Teacher
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher)

    @Query("SELECT * FROM teachers ORDER BY name ASC")
    suspend fun getAllTeachers(): List<Teacher>

    // Unit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Query("SELECT * FROM units ORDER BY name ASC")
    suspend fun getAllUnits(): List<Unit>
}


@Database(entities = [Student::class, Teacher::class, Unit::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

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


@Composable
fun StudentListFromDb(students: List<Student>) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            StudentItem("Ngô Bá Khá") // Giả định hồ sơ bạn
        }

        students.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(
                    text = initial.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(group) { student ->
                StudentItem(student.name)
            }
        }
    }
}

@Composable
fun TeacherListFromDb(teachers: List<Teacher>) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            TeacherItem("Ngô Bá Khá")
        }

        teachers.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(
                    text = initial.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(group) { teacher ->
                TeacherItem(teacher.name)
            }
        }
    }
}


@Composable
fun UnitListFromDb(units: List<Unit>) {
    LazyColumn {
        item {
            Text(
                text = "Hồ sơ của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            UnitItem("Ngô Bá Khá")
        }

        units.groupBy { it.name.first().uppercaseChar() }.forEach { (initial, group) ->
            item {
                Text(
                    text = initial.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(group) { unit ->
                UnitItem(unit.name)
            }
        }
    }
}



