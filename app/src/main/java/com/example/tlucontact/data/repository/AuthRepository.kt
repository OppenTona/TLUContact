package com.example.tlucontact.data.repository

import android.app.Activity
import android.content.Context
import com.example.tlucontact.data.model.Guest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()

        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    // Kiểm tra xem email đã được xác minh chưa
                    if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                        onResult(false, "Tài khoản chưa được xác minh. Vui lòng kiểm tra email để xác minh tài khoản.")
                        return@addOnCompleteListener
                    }

                    FirebaseAuth.getInstance().currentUser?.getIdToken(false)
                        ?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                tokenTask.result?.token?.let { firebaseToken ->
                                    // Lưu token bằng SessionManager
                                    SessionManager(context).saveUserToken(firebaseToken)
                                    onResult(true, firebaseToken)
                                } ?: onResult(false, "Token is null")
                            } else {
                                onResult(false, tokenTask.exception?.message)
                            }
                            }
                        } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signup(email: String, password: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserData(uid, email, name, phone) { success, error ->
                        if (success) {
                            // Gửi email xác thực
                            auth.currentUser?.sendEmailVerification()
                            onResult(true, null)
                        } else {
                            onResult(false, error)
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Hàm xác định loại tài khoản
    private fun detectUserType(email: String): String {
        return when {
            email.endsWith("@tlu.edu.vn") -> "staff"
            email.endsWith("@e.tlu.edu.vn") -> "student"
            else -> "guest"
        }
    }

    // Lưu dữ liệu người dùng dựa trên loại tài khoản
    private fun saveUserData(uid: String, email: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        val userType = detectUserType(email)

        if (userType == "staff" || userType == "student") {
            // Không cập nhật thông tin khi là staff hoặc student
            onResult(true, null)
        } else {
            val guest = Guest(email, phone, name, userType)
            // Thực hiện lưu thông tin nếu là guest
            val userData = mapOf(
                "uid" to guest.uid,
                "name" to guest.name,
                "email" to guest.email,
                "phone" to guest.phone,
                "userType" to "guest"
            )
            firestore.collection("guests")
                .document(email)
                .set(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }

    private fun isValidSchoolEmail(email: String): Boolean {
        return email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn")
    }

    // Login bằng tài khoản Microsoft (Outlook)
    fun loginWithMicrosoft(activity: Activity, callback: (Result<FirebaseUser>) -> Unit) {
        val provider = OAuthProvider.newBuilder("microsoft.com").apply {
            // Giả sử tenant domain của trường là "tlu.edu.vn"
            addCustomParameter("tenant", "tlu.edu.vn")
            scopes = listOf("openid", "profile", "User.Read")
        }

        if (auth.pendingAuthResult != null) {
            auth.pendingAuthResult?.addOnSuccessListener { authResult ->
                handleMicrosoftAuthResult(authResult.user, callback)
            }?.addOnFailureListener { exception ->
                callback(Result.failure(exception))
            }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    handleMicrosoftAuthResult(authResult.user, callback)
                }
                .addOnFailureListener { exception ->
                    callback(Result.failure(exception))
                }
        }
    }

    // Xử lý kết quả từ đăng nhập Microsoft:
    // Kiểm tra email trả về có đúng định dạng của trường hay không.
    private fun handleMicrosoftAuthResult(
        firebaseUser: FirebaseUser?,
        callback: (Result<FirebaseUser>) -> Unit
    ) {
        if (firebaseUser == null) {
            callback(Result.failure(Exception("Đăng nhập không thành công.")))
            return
        }
        val userEmail = firebaseUser.email?.trim() ?: ""
        if (!isValidSchoolEmail(userEmail)) {
            firebaseUser.delete()
            callback(Result.failure(Exception("Email không hợp lệ. Vui lòng sử dụng email của trường.")))
            return
        }

        // Lấy token và lưu lại
        firebaseUser.getIdToken(false)
            .addOnCompleteListener { tokenTask ->
                if (tokenTask.isSuccessful) {
                    tokenTask.result?.token?.let { firebaseToken ->
                        // Lưu token bằng SessionManager
                        SessionManager(context).saveUserToken(firebaseToken)
                        callback(Result.success(firebaseUser))
                    } ?: callback(Result.failure(Exception("Token is null")))
                } else {
                    callback(Result.failure(tokenTask.exception ?: Exception("Không thể lấy token")))
                }
            }
    }

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            onResult(false, "Vui lòng nhập email!")
            return
        }
        auth.sendPasswordResetEmail(trimmedEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Email đặt lại mật khẩu đã được gửi!")
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}