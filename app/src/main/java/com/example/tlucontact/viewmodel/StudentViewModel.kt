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
    private val db = FirebaseFirestore.getInstance()

    private val _studentList = MutableStateFlow<List<Student>>(emptyList())
    val studentList: StateFlow<List<Student>> = _studentList

    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent
    private val _filterMode = MutableStateFlow("ByName")  // "ByName" hoặc "ByClass"
    val filterMode: StateFlow<String> = _filterMode
    // Thêm trạng thái sắp xếp
    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

//    init {
//        try {
//            fetchStudents() // Lấy tất cả sinh viên ban đầu
//        }
//        catch (
//            e: Exception) {
//            println("Lỗi lấy dữ liệu sinh viên: ${e.message}")
//        }
//
//    }
    // Thêm hàm để thay đổi trạng thái sắp xếp
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }
    // Cập nhật chế độ lọc (ByName hoặc ByClass)
    fun setFilterMode(mode: String) {
        _filterMode.value = mode
        // Không cần lấy lại dữ liệu, chỉ thay đổi cách hiển thị trên UI
    }
    public fun fetchStudents(currentUserEmail: String) {
        if (currentUserEmail.endsWith("@e.tlu.edu.vn")) {
            // Nếu là sinh viên → truy xuất className trước từ chính bản thân
            db.collection("student").document(currentUserEmail).get()
                .addOnSuccessListener { doc ->
                    val className = doc.getString("className") ?: ""
                    if (className.isNotEmpty()) {
                        db.collection("student")
                            .whereEqualTo("className", className)
                            .get()
                            .addOnSuccessListener { result ->
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
                                _studentList.value = studentItems
                            }
                            .addOnFailureListener { e ->
                                println("Lỗi lấy danh sách theo lớp: ${e.message}")
                            }
                    } else {
                        println("Không tìm thấy className của sinh viên $currentUserEmail")
                    }
                }
                .addOnFailureListener { e ->
                    println("Lỗi lấy thông tin sinh viên hiện tại: ${e.message}")
                }
        } else {
            // Nếu là staff → lấy toàn bộ sinh viên
            db.collection("student").get()
                .addOnSuccessListener { result ->
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
                    _studentList.value = studentItems
                }
                .addOnFailureListener { e ->
                    println("Lỗi lấy toàn bộ sinh viên: ${e.message}")
                }
        }
    }


    fun setStudentByEmail(emailUser: String) {
        Log.d("setStudentByEmail", "Bat dau vao ham: $emailUser")
        db.collection("student").document(emailUser).get()
            .addOnSuccessListener { doc ->
                Log.d("setStudentByEmail", "lay xong du lieu: $emailUser")
                if (doc.exists()) {
                    Log.d("setStudentByEmail", "Du lieu tôn tai: $emailUser")
                    try {
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
                        println("Lỗi khi tạo đối tượng Student: ${e.message}")
                    }
                } else {
                    Log.d("setStudentByEmail", "Du lieu khong ton tai: $emailUser")
                    _selectedStudent.value = null // Nếu không tồn tại, đặt giá trị nullS
                }
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu setStudentByEmail: ${exception.message}")
            }
    }
    fun updateStudentInfo(updatedStudent: Student) {
        db.collection("student").document(updatedStudent.email)
            .set(
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
                _selectedStudent.value = updatedStudent
                _snackbarMessage.value = "Cập nhật thông tin thành công"
            }
            .addOnFailureListener { exception ->
                _snackbarMessage.value = "Lỗi cập nhật: ${exception.message}"
            }
    }
}