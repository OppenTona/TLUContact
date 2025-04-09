package com.example.tlucontact.data.repository

import android.app.Activity // Import lớp Activity từ Android
import android.content.Context // Import lớp Context từ Android
import com.example.tlucontact.data.model.Guest // Import lớp Guest từ package data.model
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth để xử lý xác thực
import com.google.firebase.auth.FirebaseUser // Import FirebaseUser để sử dụng đối tượng người dùng Firebase
import com.google.firebase.auth.OAuthProvider // Import OAuthProvider để xử lý đăng nhập bằng Microsoft
import com.google.firebase.firestore.FirebaseFirestore // Import FirebaseFirestore để tương tác với Firestore Database

// Lớp AuthRepository chịu trách nhiệm xử lý xác thực và lưu dữ liệu người dùng
class AuthRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance() // Lấy instance của FirebaseAuth để sử dụng các chức năng xác thực
    private val firestore = FirebaseFirestore.getInstance() // Lấy instance của FirebaseFirestore để thao tác với database

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

    // Hàm xử lý đăng ký tài khoản mới
    fun signup(email: String, password: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        // Gọi FirebaseAuth để tạo tài khoản mới
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình đăng ký
                if (task.isSuccessful) { // Nếu đăng ký thành công
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener // Lấy UID của người dùng đăng ký
                    saveUserData(uid, email, name, phone) { success, error -> // Lưu dữ liệu người dùng vào Firestore
                        if (success) { // Nếu lưu dữ liệu thành công
                            auth.currentUser?.sendEmailVerification() // Gửi email xác thực
                            onResult(true, null) // Trả kết quả thành công
                        } else {
                            onResult(false, error) // Trả kết quả thất bại kèm thông báo lỗi
                        }
                    }
                } else {
                    onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }

    // Hàm xác định loại tài khoản dựa trên email
    private fun detectUserType(email: String): String {
        return when { // Sử dụng when để xác định loại tài khoản
            email.endsWith("@tlu.edu.vn") -> "staff" // Nếu email kết thúc bằng @tlu.edu.vn thì loại là staff
            email.endsWith("@e.tlu.edu.vn") -> "student" // Nếu email kết thúc bằng @e.tlu.edu.vn thì loại là student
            else -> "guest" // Các trường hợp còn lại là guest
        }
    }

    // Lưu dữ liệu người dùng dựa trên loại tài khoản
    private fun saveUserData(uid: String, email: String, name: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        val userType = detectUserType(email) // Xác định loại tài khoản từ email

        if (userType == "staff" || userType == "student") { // Nếu là staff hoặc student thì không lưu dữ liệu
            onResult(true, null) // Trả kết quả thành công mà không lưu dữ liệu
        } else {
            val guest = Guest(email, phone, name, userType) // Tạo đối tượng Guest với thông tin người dùng
            // Chuẩn bị dữ liệu để lưu vào Firestore
            val userData = mapOf(
                "uid" to guest.uid, // UID của người dùng
                "name" to guest.name, // Tên của người dùng
                "email" to guest.email, // Email của người dùng
                "phone" to guest.phone, // Số điện thoại của người dùng
                "userType" to "guest" // Loại tài khoản (guest)
            )
            firestore.collection("guests") // Chọn collection "guests" trong Firestore
                .document(email) // Tạo document với ID là email
                .set(userData) // Lưu dữ liệu vào document
                .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình lưu
                    if (task.isSuccessful) { // Nếu lưu thành công
                        onResult(true, null) // Trả kết quả thành công
                    } else {
                        onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                    }
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
            scopes = listOf("openid", "profile", "User.Read") // Thêm các scope cần thiết
        }

        if (auth.pendingAuthResult != null) { // Kiểm tra nếu có phiên đăng nhập Microsoft đang chờ xử lý
            auth.pendingAuthResult?.addOnSuccessListener { authResult -> // Xử lý thành công
                handleMicrosoftAuthResult(authResult.user, callback) // Gọi hàm xử lý kết quả
            }?.addOnFailureListener { exception -> // Xử lý thất bại
                callback(Result.failure(exception)) // Trả về lỗi
            }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build()) // Bắt đầu đăng nhập với provider Microsoft
                .addOnSuccessListener { authResult -> // Xử lý thành công
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
        if (!isValidSchoolEmail(userEmail)) { // Kiểm tra nếu email không hợp lệ với định dạng trường
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

    // Hàm xử lý đặt lại mật khẩu
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        val trimmedEmail = email.trim() // Xóa khoảng trắng ở đầu và cuối email
        if (trimmedEmail.isEmpty()) { // Kiểm tra nếu email rỗng
            onResult(false, "Vui lòng nhập email!") // Trả kết quả thất bại với thông báo email rỗng
            return // Thoát khỏi hàm
        }
        auth.sendPasswordResetEmail(trimmedEmail) // Gửi email đặt lại mật khẩu qua Firebase
            .addOnCompleteListener { task -> // Lắng nghe kết quả của quá trình gửi email
                if (task.isSuccessful) { // Nếu gửi email thành công
                    onResult(true, "Email đặt lại mật khẩu đã được gửi!") // Trả kết quả thành công
                } else {
                    onResult(false, task.exception?.message) // Trả kết quả thất bại kèm thông báo lỗi
                }
            }
    }
    // Hàm xử lý đăng xuất
    fun logout(onComplete: (Boolean, String?) -> Unit) {
        try {
            auth.signOut() // Đăng xuất khỏi Firebase
            SessionManager(context).clearSession() // Xóa dữ liệu phiên làm việc
            onComplete(true, null) // Thành công
        } catch (e: Exception) {
            onComplete(false, e.message) // Thất bại
        }
    }
}
