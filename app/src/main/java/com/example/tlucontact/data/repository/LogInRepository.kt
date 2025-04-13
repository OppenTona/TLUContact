package com.example.tlucontact.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider

class LogInRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance() // Lấy instance của FirebaseAuth để sử dụng các chức năng xác thực

    // Hàm xử lý đăng nhập với email và mật khẩu
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim() // Xóa khoảng trắng ở đầu và cuối email

        // Gọi FirebaseAuth để đăng nhập bằng email và mật khẩu
        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình đăng nhập
                if (task.isSuccessful) { // Nếu đăng nhập thành công
                    val firebaseUser = auth.currentUser // Lấy thông tin người dùng hiện tại từ FirebaseAuth
                    // Kiểm tra xem email đã được xác minh chưa
                    if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                        onResult(false, "Tài khoản chưa được xác minh. Vui lòng kiểm tra email để xác minh tài khoản.") // Thông báo nếu email chưa được xác minh
                        return@addOnCompleteListener // Thoát khỏi hàm
                    }

                    // Lấy token của người dùng
                    FirebaseAuth.getInstance().currentUser?.getIdToken(false)
                        ?.addOnCompleteListener { tokenTask -> // Lắng nghe kết quả của quá trình lấy token
                            if (tokenTask.isSuccessful) { // Nếu lấy token thành công
                                tokenTask.result?.token?.let { firebaseToken -> // Lấy token từ kết quả
                                    // Lưu token bằng SessionManager
                                    SessionManager(context).saveUserToken(firebaseToken) // Lưu token vào SessionManager
                                    SessionManager(context).saveUserLoginEmail(trimmedEmail) // Lưu email vào SessionManager
                                    onResult(true, firebaseToken) // Trả kết quả thành công kèm theo token
                                } ?: onResult(false, "Token is null") // Trả kết quả thất bại nếu token null
                            } else {
                                onResult(false, tokenTask.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                            }
                        }
                } else {
                    onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }

    // Hàm kiểm tra email có phải email của trường không
    private fun isValidSchoolEmail(email: String): Boolean {
        return email.endsWith("@tlu.edu.vn") || email.endsWith("@e.tlu.edu.vn") // Trả về true nếu email thuộc domain của trường
    }

    // Hàm đăng nhập bằng tài khoản Microsoft (Outlook)
    fun loginWithMicrosoft(activity: Activity, callback: (Result<FirebaseUser>) -> Unit) {
        val provider = OAuthProvider.newBuilder("microsoft.com").apply { // Tạo provider OAuth cho Microsoft
            addCustomParameter("tenant", "tlu.edu.vn") // Thêm tham số tenant domain
            scopes = listOf("openid", "profile", "User.Read", "email") // Thêm các scope cần thiết
        }

        if (auth.pendingAuthResult != null) { // Kiểm tra nếu có phiên đăng nhập Microsoft đang chờ xử lý
            auth.pendingAuthResult?.addOnSuccessListener { authResult ->
                Log.d("PROFILE_RAW", "Raw profile: ${authResult.additionalUserInfo?.profile ?: "null"}") // Xử lý thành công
                handleMicrosoftAuthResult(authResult.user, callback) // Gọi hàm xử lý kết quả
            }?.addOnFailureListener { exception -> // Xử lý thất bại
                callback(Result.failure(exception)) // Trả về lỗi
            }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build()) // Bắt đầu đăng nhập với provider Microsoft
                .addOnSuccessListener { authResult ->
                    Log.d("PROFILE_RAW", "Raw profile: ${authResult.additionalUserInfo?.profile ?: "null"}") // Xử lý thành công
                    handleMicrosoftAuthResult(authResult.user, callback) // Gọi hàm xử lý kết quả
                }
                .addOnFailureListener { exception -> // Xử lý thất bại
                    callback(Result.failure(exception)) // Trả về lỗi
                }
        }
    }

    // Hàm xử lý kết quả từ đăng nhập Microsoft
    private fun handleMicrosoftAuthResult(
        firebaseUser: FirebaseUser?, // Đối tượng người dùng Firebase trả về từ Microsoft
        callback: (Result<FirebaseUser>) -> Unit // Callback để trả kết quả
    ) {
        if (firebaseUser == null) { // Kiểm tra nếu người dùng là null
            callback(Result.failure(Exception("Đăng nhập không thành công."))) // Trả về lỗi đăng nhập không thành công
            return // Thoát khỏi hàm
        }
        val userEmail = firebaseUser.email?.trim() ?: "" // Lấy email người dùng và xóa khoảng trắng
        Log.d("USEREMAIL", "❌ UserMEail laf: ${userEmail}")
        if (!isValidSchoolEmail(userEmail)) { // Kiểm tra nếu email không hợp lệ với định dạng trường
            Log.d("USEREMAIL", "❌ UserMEail laf: ${userEmail}")
            firebaseUser.delete() // Xóa tài khoản người dùng
            callback(Result.failure(Exception("Email không hợp lệ. Vui lòng sử dụng email của trường."))) // Trả về lỗi email không hợp lệ
            return // Thoát khỏi hàm
        }

        // Lấy token của người dùng và lưu lại
        firebaseUser.getIdToken(false)
            .addOnCompleteListener { tokenTask -> // Lắng nghe kết quả lấy token
                if (tokenTask.isSuccessful) { // Nếu lấy token thành công
                    tokenTask.result?.token?.let { firebaseToken -> // Lấy token từ kết quả
                        SessionManager(context).saveUserToken(firebaseToken) // Lưu token vào SessionManager
                        callback(Result.success(firebaseUser)) // Trả kết quả thành công với người dùng Firebase
                    } ?: callback(Result.failure(Exception("Token is null"))) // Trả kết quả thất bại nếu token null
                } else {
                    callback(Result.failure(tokenTask.exception ?: Exception("Không thể lấy token"))) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }
}