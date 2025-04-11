package com.example.tlucontact.data.repository

import com.example.tlucontact.data.model.Guest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GuestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val guestCollection = db.collection("guests")

    // Lấy thông tin của chính guest đang đăng nhập
    fun getGuestByEmail(email: String, onResult: (Guest?) -> Unit) {
        db.collection("guests").document(email).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val guest = Guest(
                        userId = doc.getString("userId") ?: "",
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        avatarURL = doc.getString("avatarURL") ?: "",
                        position = doc.getString("position") ?: "",
                        department = doc.getString("department") ?: "",
                        address = doc.getString("address") ?: "",
                        userType = doc.getString("userType") ?: ""
                    )
                    onResult(guest)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Cập nhật thông tin guest
    suspend fun updateGuest(guest: Guest): Result<Unit> {
        return try {
            guestCollection.document(guest.email).set(guest).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
