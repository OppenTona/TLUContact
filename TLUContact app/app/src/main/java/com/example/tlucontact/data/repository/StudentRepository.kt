package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StudentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val studentCollection = db.collection("students")

    // Lấy danh sách sinh viên từ Firestore
    suspend fun getStudents(): List<Student> {
        return try {
            val snapshot = studentCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Student::class.java)?.copy(studentID = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Lấy thông tin sinh viên theo ID
    suspend fun getStudentById(studentId: String): Student? {
        return try {
            val doc = studentCollection.document(studentId).get().await()
            doc.toObject(Student::class.java)?.copy(studentID = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Cập nhật thông tin sinh viên
    suspend fun updateStudent(student: Student) {
        studentCollection.document(student.studentID).set(student).await()
    }
}
