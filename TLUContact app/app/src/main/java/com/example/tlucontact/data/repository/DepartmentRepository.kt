package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Department
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DepartmentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val departmentCollection = db.collection("department")

    // Lấy danh sách đơn vị từ Firestore
    suspend fun getDepartments(): List<Department> {
        return try {
            val snapshot = departmentCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Department::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Lấy thông tin đơn vị theo ID
    suspend fun getDepartmentById(departmentId: String): Department? {
        return try {
            val doc = departmentCollection.document(departmentId).get().await()
            doc.toObject(Department::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
}
