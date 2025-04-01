package com.example.tlucontact.data.repository

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.tlucontact.MainActivity
import com.example.tlucontact.data.model.User
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val provider = OAuthProvider.newBuilder("microsoft.com")

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
                    FirebaseAuth.getInstance().currentUser?.getIdToken(false)
                        ?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val firebaseToken = tokenTask.result?.token
                                // Save token using SessionManager
                                val sessionManager = SessionManager(context)
                                if (firebaseToken != null) {
                                    sessionManager.saveUserToken(firebaseToken)
                                    onResult(true, firebaseToken)
                                }
                                else {
                                    onResult(false, "Token is null")
                                }
                            } else {
                                onResult(false, tokenTask.exception?.message)
                            }
                            }
                        } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signup(user: User, password: String, confirmPassword: String, onResult: (Boolean, String?) -> Unit) {
        val email = user.email.trim()

        val error = validateSignupInput(email, password, confirmPassword)
        if (error != null) {
            onResult(false, error)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { fbUser ->
                        saveUserData(fbUser.uid, user) { success, saveError ->
                            if (success) {
                                sendEmailVerification(fbUser) { emailSuccess, emailError ->
                                    onResult(emailSuccess, emailError)
                                }
                            } else {
                                onResult(false, saveError)
                            }
                        }
                    }
                } else {
                    onResult(false, task.exception?.message)
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

    private fun validateSignupInput(email: String, password: String, confirmPassword: String): String? {
        val basicError = validateCredentials(email, password)
        if (basicError != null) return basicError
        if (password != confirmPassword) return "Mật khẩu không khớp"
        if (!(email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn"))) return "Email không hợp lệ. Vui lòng sử dụng email của trường."
        return null
    }

    private fun saveUserData(uid: String, user: User, onResult: (Boolean, String?) -> Unit) {
        val userType = if (user.email.endsWith("@tlu.edu.vn")) "lecturer" else "student"
        val userData = hashMapOf(
            "uid" to uid,
            "email" to user.email,
            "phone" to user.phone,
            "userType" to userType
        )

        firestore.collection("users").document(uid).set(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

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

    // Giả định hàm gửi email xác nhận, ví dụ tích hợp với Firebase.
    fun sendEmailVerificationLink(email: String, onResult: (Boolean, String?) -> Unit) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            // Chỉnh sửa URL dưới đây thành URL deep link của ứng dụng bạn
            .setUrl("https://yourapp.page.link/verify?email=$email")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.tlucontact", true, null)
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Giả định reload trạng thái người dùng để kiểm tra email đã xác nhận hay chưa.
    fun reloadUser(email: String, callback: (Boolean, String?) -> Unit) {
        // Reload thông tin người dùng (ví dụ FirebaseAuth.getCurrentUser().reload()).
        // Nếu email đã xác nhận:
        callback(true, null)
        // Nếu chưa xác nhận hoặc lỗi:
        // callback(false, "Email chưa được xác nhận!")
    }

    // Giả định hàm tạo tài khoản sau khi email được xác nhận.
    fun createAccount(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        // Tạo tài khoản với thông tin đã cung cấp.
        // Nếu tạo tài khoản thành công:
        callback(true, null)
        // Nếu không:
        // callback(false, "Tạo tài khoản thất bại!")
    }

    fun fetchFirebaseToken(onTokenReceived: (token: String?) -> Unit, onError: (Exception) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUser.getIdToken(false)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Lấy token thành công
                        val token = task.result?.token
                        onTokenReceived(token)
                    } else {
                        // Xử lý khi có lỗi
                        onError(task.exception ?: Exception("Unknown error"))
                    }
                }
        } else {
            onError(Exception("User is not logged in."))
        }
    }

    // Đăng ký bằng tài khoản Microsoft
    fun signUpWithMicrosoft(activity: Activity, callback: (Result<FirebaseUser>) -> Unit) {
        auth.startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Kiểm tra email hợp lệ trước
                    if (isValidOutlookEmail(user.email ?: "")) {
                        // Nếu email chưa được xác thực, gửi email xác thực
                        if (!user.isEmailVerified) {
                            user.sendEmailVerification().addOnCompleteListener {
                                // Sau khi gửi email, trả về kết quả thành công
                                // (với thông báo cần xác thực email)
                                callback(Result.success(user))
                            }
                        } else {
                            // Nếu email đã xác thực, trả về kết quả thành công trực tiếp
                            callback(Result.success(user))
                        }
                    } else {
                        // Nếu email không hợp lệ, đăng xuất và trả về lỗi
                        auth.signOut()
                        callback(Result.failure(Exception("Email không hợp lệ.")))
                    }
                } else {
                    callback(Result.failure(Exception("Đăng ký không thành công.")))
                }
            }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    // Kiểm tra xem email có hợp lệ theo tiêu chí của bạn hay không
    fun isValidOutlookEmail(email: String): Boolean {
        return email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn")
    }
    // Hàm kiểm tra định dạng email của trường, chỉ chấp nhận @tlu.edu.vn và @e.tlu.edu.vn
    private fun isValidSchoolEmail(email: String): Boolean {
        return email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn")
    }

    /**
     * Đăng ký bằng Microsoft.
     *
     * Lưu ý:
     * - Người dùng sẽ được chuyển qua màn hình đăng nhập của Microsoft.
     * - Sau khi đăng nhập thành công, Firebase sẽ tạo tài khoản mới nếu chưa tồn tại.
     * - Bạn có thể kiểm tra định dạng email ngay từ thông tin người dùng trả về,
     *   nếu không đúng định dạng, từ chối đăng ký và xóa tài khoản vừa tạo.
     */
    fun signupWithMicrosoft(activity: Activity, user: User, onResult: (Boolean, String?) -> Unit) {
        // Nếu user.email được cung cấp trước (ví dụ người dùng nhập sẵn email) ta kiểm tra định dạng
        val email = user.email.trim()
        if (!isValidSchoolEmail(email)) {
            onResult(false, "Email không hợp lệ. Vui lòng sử dụng email của trường.")
            return
        }

        // Tạo OAuth provider cho microsoft.com
        val provider = OAuthProvider.newBuilder("microsoft.com")
        // Thêm tùy chọn nếu cần (ví dụ tenant, scope, v.v.)
        provider.addCustomParameter("tenant", "common")
        provider.scopes = listOf("openid", "profile", "User.Read")

        // Nếu đã có một tác vụ đang chờ (pending) thì sử dụng kết quả đó
        if (auth.pendingAuthResult != null) {
            auth.pendingAuthResult
                ?.addOnSuccessListener { authResult ->
                    handleMicrosoftAuthResult(authResult.user, user, onResult)
                }
                ?.addOnFailureListener { exception ->
                    onResult(false, exception.message)
                }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    handleMicrosoftAuthResult(authResult.user, user, onResult)
                }
                .addOnFailureListener { exception ->
                    onResult(false, exception.message)
                }
        }
    }

    // Hàm xử lý kết quả từ đăng nhập Microsoft và lưu dữ liệu lên Firestore
    private fun handleMicrosoftAuthResult(firebaseUser: com.google.firebase.auth.FirebaseUser?, user: User, onResult: (Boolean, String?) -> Unit) {
        if (firebaseUser == null) {
            onResult(false, "Đăng nhập không thành công.")
            return
        }

        // Nếu muốn kiểm tra email trả về từ tài khoản Microsoft, đảm bảo rằng nó đúng định dạng
        val userEmail = firebaseUser.email?.trim() ?: ""
        if (!isValidSchoolEmail(userEmail)) {
            // Nếu email không hợp lệ, bạn có thể xóa tài khoản vừa tạo
            firebaseUser.delete()
            onResult(false, "Email không hợp lệ. Vui lòng sử dụng email của trường.")
            return
        }

        // Nếu đã kiểm tra và email hợp lệ, lưu thông tin user lên Firestore
        saveUserData(firebaseUser.uid, user) { success, saveError ->
            if (success) {
                // Có thể thực hiện các bước bổ sung như gửi email xác thực nếu cần.
                onResult(true, null)
            } else {
                onResult(false, saveError)
            }
        }
    }
}