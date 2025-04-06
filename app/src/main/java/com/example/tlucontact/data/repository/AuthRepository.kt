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
    private val functions = FirebaseFunctions.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        val error = validateCredentials(trimmedEmail, trimmedPassword)
        if (error != null) {
            onResult(false, error)
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
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

    fun signup(email: String, password: String, confirmPassword: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        val error = validateSignupInput(email, password, confirmPassword, name, phone)
        if (error != null) {
            onResult(false, error)
            return
        }

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

    private fun validateCredentials(email: String, password: String): String? {
        if (email.isEmpty() || password.isEmpty()) return "Vui lòng nhập email và mật khẩu"
        if (password.length < 6) return "Mật khẩu phải có ít nhất 6 ký tự"
        if (password.contains(email)) return "Mật khẩu không được chứa email"
        if (password.contains(" ")) return "Mật khẩu không được chứa khoảng trắng"
        return null
    }

    private fun isValidSchoolEmail(email: String): Boolean {
        return email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn")
    }

    private fun validateSignupInput(email: String, password: String, confirmPassword: String, name: String, phone: String): String? {
        val basicError = validateCredentials(email, password)
        if (basicError != null) return basicError
        if (password != confirmPassword) return "Mật khẩu không khớp"
        if (!isValidSchoolEmail(email) && name == null && phone == null ) return "Hãy nhập thông tin đẩy đủ"
        return null
    }

//    private fun saveUserData(uid: String, guest: Guest, onResult: (Boolean, String?) -> Unit) {
//        val userEmail = guest.email.trim()
//        val (collectionName, userType) = when {
//            userEmail.endsWith("@tlu.edu.vn") -> "staff" to "staff"
//            userEmail.endsWith("@e.tlu.edu.vn") -> "student" to "student"
//            else -> "guests" to "guest"
//        }
//
//        val userData = hashMapOf(
//            "name" to guest.name,
//            "email" to userEmail,
//            "phone" to guest.phone,
//            "userType" to userType
//        )
//
//        // Sử dụng email làm document ID như yêu cầu
//        firestore.collection(collectionName).document(userEmail).set(userData)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onResult(true, null)
//                } else {
//                    onResult(false, task.exception?.message)
//                }
//            }
//    }

    private fun sendEmailVerification(user: FirebaseUser, onResult: (Boolean, String?) -> Unit) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
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