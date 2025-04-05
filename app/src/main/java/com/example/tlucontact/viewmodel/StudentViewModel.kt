package com.example.tlucontact.viewmodel

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

    init {
        try {
            fetchStudents() // Lấy tất cả sinh viên ban đầu
        }
        catch (
            e: Exception) {
            println("Lỗi lấy dữ liệu sinh viên: ${e.message}")
        }

    }

    private fun fetchStudents() {
//        db.collection("student")
//            .get()
//        .addOnSuccessListener { result ->
//            for (document in result) {
//                println("${document.id} => ${document.data}") // In dữ liệu
//            }
//        }
//            .addOnFailureListener { exception ->
//                println("Lỗi khi lấy dữ liệu: ${exception.message}")
//            }



        db.collection("student").get()
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
                println("Lỗi lấy dữ liệu: ${exception.message}")
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
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }

    fun setSelectedStudent(studentId: String) {
        db.collection("students").document(studentId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _selectedStudent.value = Student(
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
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }
}