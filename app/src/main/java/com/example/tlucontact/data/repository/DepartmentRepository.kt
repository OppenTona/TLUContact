package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Department
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DepartmentRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getDepartments(): List<Department> {
        return try {
            // Lấy danh sách các đơn vị từ Firestore
            val snapshot = db.collection("department").get().await()

            snapshot.documents.map { doc ->
                doc.toObject(Department::class.java) ?: Department()
            }
        } catch (e: Exception) {
            emptyList() // Nếu có lỗi thì trả về danh sách trống
        }
    }
}
