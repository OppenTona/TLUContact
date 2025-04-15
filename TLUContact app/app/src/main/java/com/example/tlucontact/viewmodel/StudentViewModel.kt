package com.example.tlucontact.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    // Khởi tạo đối tượng Firestore để thao tác với cơ sở dữ liệu Firebase
    private val db = FirebaseFirestore.getInstance()

    // MutableStateFlow lưu trữ danh sách sinh viên, ban đầu rỗng
    private val _studentList = MutableStateFlow<List<Student>>(emptyList())
    // StateFlow bất biến để UI quan sát (observe) danh sách sinh viên
    val studentList: StateFlow<List<Student>> = _studentList

    // MutableStateFlow lưu trữ sinh viên đang được chọn, ban đầu là null
    private val _selectedStudent = MutableStateFlow<Student?>(null)
    // StateFlow bất biến để UI quan sát sinh viên đang được chọn
    val selectedStudent: StateFlow<Student?> = _selectedStudent

    // MutableStateFlow lưu trữ chế độ lọc ("ByName" hoặc "ByClass")
    private val _filterMode = MutableStateFlow("ByName")
    // StateFlow bất biến để UI quan sát chế độ lọc hiện tại
    val filterMode: StateFlow<String> = _filterMode

    // MutableStateFlow lưu trữ trạng thái sắp xếp (true: tăng dần, false: giảm dần)
    private val _sortAscending = MutableStateFlow(true)
    // StateFlow bất biến để UI quan sát trạng thái sắp xếp
    val sortAscending: StateFlow<Boolean> = _sortAscending

    // MutableStateFlow lưu trữ thông báo cho Snackbar, ban đầu null (không có thông báo)
    private val _snackbarMessage = MutableStateFlow<String?>(null)

    // Hàm đảo ngược trạng thái sắp xếp (từ tăng dần sang giảm dần hoặc ngược lại)
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }

    // Hàm cập nhật chế độ lọc (ByName hoặc ByClass)
    fun setFilterMode(mode: String) {
        _filterMode.value = mode
        // Không cần lấy lại dữ liệu, chỉ thay đổi cách hiển thị trên UI
    }

    // Hàm lấy danh sách sinh viên từ Firebase dựa trên email của người dùng hiện tại
    public fun fetchStudents(currentUserEmail: String) {
        if (currentUserEmail.endsWith("@e.tlu.edu.vn")) {
            // Nếu là sinh viên → truy xuất className trước từ chính bản thân
            db.collection("student").document(currentUserEmail).get()
                .addOnSuccessListener { doc ->
                    // Lấy tên lớp của sinh viên hiện tại
                    val className = doc.getString("className") ?: ""
                    if (className.isNotEmpty()) {
                        // Nếu có tên lớp, truy vấn tất cả sinh viên cùng lớp
                        db.collection("student")
                            .whereEqualTo("className", className)
                            .get()
                            .addOnSuccessListener { result ->
                                // Chuyển đổi các document thành đối tượng Student
                                val studentItems = result.map { studentDoc ->
                                    Student(
                                        studentID = studentDoc.getString("studentID") ?: "",
                                        fullNameStudent = studentDoc.getString("fullNameStudent") ?: "Không có tên",
                                        photoURL = studentDoc.getString("photoURL") ?: "",
                                        email = studentDoc.getString("email") ?: "",
                                        phone = studentDoc.getString("phone") ?: "",
                                        address = studentDoc.getString("address") ?: "",
                                        className = studentDoc.getString("className") ?: "",
                                        userID = studentDoc.getString("userID") ?: ""
                                    )
                                }
                                // Cập nhật danh sách sinh viên
                                _studentList.value = studentItems
                            }
                            .addOnFailureListener { e ->
                                // Xử lý lỗi khi truy vấn danh sách sinh viên
                                println("Lỗi lấy danh sách theo lớp: ${e.message}")
                            }
                    } else {
                        // Xử lý trường hợp không tìm thấy tên lớp
                        println("Không tìm thấy className của sinh viên $currentUserEmail")
                    }
                }
                .addOnFailureListener { e ->
                    // Xử lý lỗi khi lấy thông tin sinh viên hiện tại
                    println("Lỗi lấy thông tin sinh viên hiện tại: ${e.message}")
                }
        } else {
            // Nếu là staff → lấy toàn bộ sinh viên
            db.collection("student").get()
                .addOnSuccessListener { result ->
                    // Chuyển đổi tất cả document thành đối tượng Student
                    val studentItems = result.map { doc ->
                        Student(
                            studentID = doc.getString("studentID") ?: "",
                            fullNameStudent = doc.getString("fullNameStudent") ?: "Không có tên",
                            photoURL = doc.getString("photoURL") ?: "",
                            email = doc.getString("email") ?: "",
                            phone = doc.getString("phone") ?: "",
                            address = doc.getString("address") ?: "",
                            className = doc.getString("className") ?: "",
                            userID = doc.getString("userID") ?: ""
                        )
                    }
                    // Cập nhật danh sách sinh viên
                    _studentList.value = studentItems
                }
                .addOnFailureListener { e ->
                    // Xử lý lỗi khi lấy toàn bộ sinh viên
                    println("Lỗi lấy toàn bộ sinh viên: ${e.message}")
                }
        }
    }

    // Hàm lấy thông tin chi tiết của sinh viên theo email
    fun setStudentByEmail(emailUser: String) {
        // Ghi log để debug
        Log.d("setStudentByEmail", "Bat dau vao ham: $emailUser")
        // Truy vấn document của sinh viên theo email
        db.collection("student").document(emailUser).get()
            .addOnSuccessListener { doc ->
                // Ghi log khi lấy dữ liệu thành công
                Log.d("setStudentByEmail", "lay xong du lieu: $emailUser")
                if (doc.exists()) {
                    // Ghi log khi document tồn tại
                    Log.d("setStudentByEmail", "Du lieu tôn tai: $emailUser")
                    try {
                        // Tạo đối tượng Student từ document
                        _selectedStudent.value = Student(
                            studentID = doc.getString("studentID") ?: "",
                            fullNameStudent = doc.getString("fullNameStudent") ?: "Không có tên",
                            photoURL = doc.getString("photoURL") ?: "",
                            email = doc.getString("email") ?: "",
                            phone = doc.getString("phone") ?: "",
                            address = doc.getString("address") ?: "",
                            className = doc.getString("className") ?: "",
                            userID = doc.getString("userID") ?: ""
                        )
                    } catch (e: Exception) {
                        // Xử lý lỗi khi tạo đối tượng Student
                        println("Lỗi khi tạo đối tượng Student: ${e.message}")
                    }
                } else {
                    // Ghi log khi document không tồn tại
                    Log.d("setStudentByEmail", "Du lieu khong ton tai: $emailUser")
                    // Đặt giá trị null cho sinh viên được chọn
                    _selectedStudent.value = null
                }
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi khi lấy dữ liệu
                println("Lỗi lấy dữ liệu setStudentByEmail: ${exception.message}")
            }
    }

    // Hàm cập nhật thông tin sinh viên trong Firebase
    fun updateStudentInfo(updatedStudent: Student) {
        // Cập nhật document sinh viên theo email
        db.collection("student").document(updatedStudent.email)
            .set(
                // Tạo map chứa các thông tin cần cập nhật
                mapOf(
                    "fullNameStudent" to updatedStudent.fullNameStudent,
                    "phone" to updatedStudent.phone,
                    "studentID" to updatedStudent.studentID,
                    "userID" to updatedStudent.userID,
                    "email" to updatedStudent.email,
                    "address" to updatedStudent.address,
                    "className" to updatedStudent.className,
                    "photoURL" to updatedStudent.photoURL
                )
            )
            .addOnSuccessListener {
                // Cập nhật đối tượng sinh viên đã chọn
                _selectedStudent.value = updatedStudent
                // Hiển thị thông báo thành công
                _snackbarMessage.value = "Cập nhật thông tin thành công"
            }
            .addOnFailureListener { exception ->
                // Hiển thị thông báo lỗi
                _snackbarMessage.value = "Lỗi cập nhật: ${exception.message}"
            }
    }

}