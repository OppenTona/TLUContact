//package com.example.tlucontact.data.repository
//
//import com.example.tlucontact.data.model.Department
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class DepartmentRepository {
//
//    private val db = FirebaseFirestore.getInstance()
//
//    suspend fun getDepartments(): List<Department> {
//        return try {
//            val snapshot = db.collection("department").get().await()
//            snapshot.documents.map { doc ->
//                doc.toObject(Department::class.java) ?: Department()
//            }
//        } catch (e: Exception) {
//            emptyList() // Trả về danh sách trống nếu có lỗi
//        }
//    }
//}

package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Department
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DepartmentRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getDepartments(): List<Department> {
        val result = db.collection("department").get().await()
        return result.map { doc ->
            Department(
                id = doc.getString ("id") ?: "",
                name = doc.getString("name") ?: "Không có tên",
                leader = doc.getString("leader") ?: "",
                email = doc.getString("email") ?: "",
                phone = doc.getString("phone") ?: "",
                address = doc.getString("address") ?: "",
                photoURL = doc.getString("photoURL") ?: ""
            )
        }
    }
}