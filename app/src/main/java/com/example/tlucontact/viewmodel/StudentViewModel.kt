package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _studentList = MutableStateFlow<List<Student>>(emptyList())
    val studentList: StateFlow<List<Student>> = _studentList

    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent

    init {
        fetchStudents()
    }

    private fun fetchStudents() {
        db.collection("students").get()
            .addOnSuccessListener { result ->
                val studentItems = result.map { doc ->
                    Student(
                        studentID = doc.id,
                        fullNameStudent = doc.getString("fullNameStudent") ?: "Không có tên",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        className = doc.getString("className") ?: "",
                        userID = doc.getString("userID") ?: "",
                        photoURL = doc.getString("photoURL") ?: ""
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
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        className = doc.getString("className") ?: "",
                        userID = doc.getString("userID") ?: "",
                        photoURL = doc.getString("photoURL") ?: ""
                    )
                }
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }
}