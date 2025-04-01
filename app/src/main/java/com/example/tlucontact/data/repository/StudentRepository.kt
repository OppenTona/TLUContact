package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore

class StudentRepository {
    // Tạo một đối tượng Singleton cho StudentRepository
    companion object {
        @Volatile
        private var instance: StudentRepository? = null

        fun getInstance(): StudentRepository {
            return instance ?: synchronized(this) {
                instance ?: StudentRepository().also { instance = it }
            }
        }
    }

    // Hàm để lấy danh sách sinh viên từ Firestore
    fun getStudentList(onResult: (List<Student>) -> Unit) {
        val studentList = mutableListOf<Student>()
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("students")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val student = document.toObject(Student::class.java)
                    studentList.add(student)
                }
                onResult(studentList)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList())
            }
    }
}