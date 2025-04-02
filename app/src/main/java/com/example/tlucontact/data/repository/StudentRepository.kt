package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StudentRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val studentCollection = firestore.collection("students")

    // Lấy ID của người dùng hiện tại
    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // Thêm hoặc cập nhật thông tin sinh viên (chỉ của người dùng hiện tại)
    suspend fun addOrUpdateStudent(student: Student): Boolean {
        val userId = getUserId() ?: return false
        return try {
            studentCollection.document(userId).set(student).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Lấy thông tin sinh viên của người dùng hiện tại
    fun getStudent(): Flow<Student?> = flow {
        val userId = getUserId() ?: return@flow
        val snapshot = studentCollection.document(userId).get().await()
        val student = snapshot.toObject(Student::class.java)
        emit(student)
    }

    // Xóa thông tin sinh viên của người dùng hiện tại
    suspend fun deleteStudent(): Boolean {
        val userId = getUserId() ?: return false
        return try {
            studentCollection.document(userId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Lấy danh sách tất cả sinh viên, sắp xếp theo tên
    fun getAllStudents(): Flow<List<Student>> = flow {
        val snapshot = studentCollection.orderBy("name").get().await()
        val students = snapshot.documents.mapNotNull { it.toObject(Student::class.java) }
        emit(students)
    }
}