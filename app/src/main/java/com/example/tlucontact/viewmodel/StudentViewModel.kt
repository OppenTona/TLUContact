package com.example.tlucontact.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore

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

    init {
        try {
            fetchStudents() // Lấy tất cả sinh viên ban đầu
        }
        catch (
            e: Exception) {
            println("Lỗi lấy dữ liệu sinh viên: ${e.message}")
        }

    }
    // Thêm hàm để thay đổi trạng thái sắp xếp
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }
    // Cập nhật chế độ lọc (ByName hoặc ByClass)
    fun setFilterMode(mode: String) {
        _filterMode.value = mode
        // Không cần lấy lại dữ liệu, chỉ thay đổi cách hiển thị trên UI
    }
    private fun fetchStudents() {
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
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu fetchStudents: ${exception.message}")
            }
    }

    // Phương thức lọc sinh viên theo className (Lớp)
    fun filterStudents(className: String) {
        db.collection("students")
            .whereEqualTo("className", className) // Lọc theo lớp
            .get()
            .addOnSuccessListener { result ->
                val studentItems = result.map { doc ->
                    Student(
                        studentID = doc.id,
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
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu filterStudents: ${exception.message}")
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
        // Cập nhật thông tin sinh viên vào Firestore theo email
        db.collection("student").document(updatedStudent.email)
            .set(
                mapOf(
                    "fullNameStudent" to updatedStudent.fullNameStudent,
                    "phone" to updatedStudent.phone,
                    "address" to updatedStudent.address,
                    "className" to updatedStudent.className,
                    "photoURL" to updatedStudent.photoURL
                )
            )
            .addOnSuccessListener {
                // Cập nhật thành công
                _selectedStudent.value = updatedStudent // Cập nhật giá trị mới vào _selectedStudent
                println("Cập nhật thông tin sinh viên thành công")
            }
            .addOnFailureListener { exception ->
                // Lỗi cập nhật
                println("Lỗi cập nhật thông tin sinh viên: ${exception.message}")
            }
    }
}