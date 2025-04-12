package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Department
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DepartmentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val departmentCollection = db.collection("department")

    suspend fun getDepartments(): List<Department> {
        val result = departmentCollection.get().await()
        return result.map { doc ->
            Department(
                id = doc.getString("id") ?: "",
                name = doc.getString("name") ?: "Không có tên",
                leader = doc.getString("leader") ?: "",
                email = doc.getString("email") ?: "",
                phone = doc.getString("phone") ?: "",
                address = doc.getString("address") ?: "",
                photoURL = doc.getString("photoURL") ?: "",
                type = doc.getString("type") ?: ""
            )
        }
    }

    suspend fun getDepartmentById(departmentId: String): Department? {
        return try {
            val document = departmentCollection.document(departmentId).get().await()
            if (document.exists()) {
                document.toObject(Department::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Lỗi lấy thông tin đơn vị: ${e.message}")
            null
        }
    }

    suspend fun updateDepartment(department: Department) {
        try {
            departmentCollection.document(department.id).set(department).await()
        } catch (e: Exception) {
            println("Lỗi cập nhật thông tin đơn vị: ${e.message}")
            throw e // Re-throw the exception to be handled in ViewModel
        }
    }
}