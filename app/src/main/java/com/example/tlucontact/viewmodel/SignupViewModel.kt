package com.example.tlucontact.viewmodel // Định nghĩa package của lớp này

import android.app.Application // Import class Application từ Android
import android.widget.Toast // Import Toast để hiển thị thông báo nhanh trên giao diện
import androidx.lifecycle.AndroidViewModel // Import lớp AndroidViewModel để tạo ViewModel có tham chiếu đến Application
import androidx.lifecycle.SavedStateHandle // Import SavedStateHandle để lưu và khôi phục trạng thái
import androidx.lifecycle.viewModelScope // Import viewModelScope để sử dụng Coroutine trong ViewModel
import com.example.tlucontact.data.repository.AuthRepository // Import AuthRepository để xử lý logic xác thực
import kotlinx.coroutines.flow.MutableStateFlow // Import MutableStateFlow để lưu trữ và quan sát giá trị trạng thái
import kotlinx.coroutines.flow.asStateFlow // Import hàm asStateFlow để tạo StateFlow từ MutableStateFlow
import kotlinx.coroutines.launch // Import launch để khởi chạy Coroutine
import kotlin.text.contains // Import hàm contains để kiểm tra chuỗi con
import kotlin.text.endsWith // Import hàm endsWith để kiểm tra chuỗi kết thúc
import kotlin.text.isEmpty // Import hàm isEmpty để kiểm tra chuỗi trống
import kotlin.text.startsWith // Import hàm startsWith để kiểm tra chuỗi bắt đầu

// Định nghĩa lớp SignupViewModel kế thừa từ AndroidViewModel
class SignupViewModel(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    private val repository = AuthRepository(application) // Khởi tạo repository để xử lý logic xác thực

    // Biến trạng thái lưu trữ trạng thái đăng ký và thông báo lỗi (nếu có)
    private val _signupState = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, null))
    val signupState = _signupState.asStateFlow() // Biến public để quan sát trạng thái đăng ký

    // Biến trạng thái kiểm tra email hợp lệ hay không
    private val _isEmailInvalid = MutableStateFlow(true)
    val isEmailInvalid = _isEmailInvalid.asStateFlow() // Biến public để quan sát trạng thái email hợp lệ

    // Biến trạng thái kiểm tra lỗi email
    private val _emailError = MutableStateFlow(false)
    val emailError = _emailError.asStateFlow() // Biến public để quan sát trạng thái lỗi email

    // Biến trạng thái kiểm tra lỗi mật khẩu
    private val _passwordError = MutableStateFlow(false)
    val passwordError = _passwordError.asStateFlow() // Biến public để quan sát trạng thái lỗi mật khẩu

    // Biến trạng thái kiểm tra lỗi xác nhận mật khẩu
    private val _confirmPasswordError = MutableStateFlow(false)
    val confirmPasswordError = _confirmPasswordError.asStateFlow() // Biến public để quan sát trạng thái lỗi xác nhận mật khẩu

    // Biến trạng thái kiểm tra lỗi tên
    private val _nameError = MutableStateFlow(false)
    val nameError = _nameError.asStateFlow() // Biến public để quan sát trạng thái lỗi tên

    // Biến trạng thái kiểm tra lỗi số điện thoại
    private val _phoneError = MutableStateFlow(false)
    val phoneError = _phoneError.asStateFlow() // Biến public để quan sát trạng thái lỗi số điện thoại

    // Biến lưu trữ và khôi phục trạng thái của tên
    var name = state.getStateFlow("name", "")
    // Biến lưu trữ và khôi phục trạng thái của số điện thoại
    var phone = state.getStateFlow("phone", "")
    // Biến lưu trữ và khôi phục trạng thái của email
    var email = state.getStateFlow("email", "")
    // Biến lưu trữ và khôi phục trạng thái của mật khẩu
    var password = state.getStateFlow("password", "")
    // Biến lưu trữ và khôi phục trạng thái của xác nhận mật khẩu
    var confirmPassword = state.getStateFlow("confirmPassword", "")

    // Hàm thực hiện đăng ký tài khoản
    fun signup() {
        viewModelScope.launch { // Sử dụng Coroutine để chạy hàm bất đồng bộ
            val errorMsg = validateInputs() // Gọi hàm kiểm tra tính hợp lệ của đầu vào
            if (errorMsg != null) { // Nếu có lỗi đầu vào
                _signupState.value = Pair(false, errorMsg) // Cập nhật trạng thái đăng ký thất bại và thông báo lỗi
                return@launch // Thoát khỏi Coroutine
            }

            // Nếu không có lỗi, tiến hành đăng ký
            repository.signup(
                email = email.value, // Truyền email từ trạng thái email
                password = password.value, // Truyền mật khẩu từ trạng thái password
                name = name.value, // Truyền tên từ trạng thái name
                phone = phone.value // Truyền số điện thoại từ trạng thái phone
            ) { success, error -> // Callback xử lý kết quả đăng ký
                _signupState.value = Pair(success, error) // Cập nhật trạng thái đăng ký thành công hoặc thất bại
                if (!success) { // Nếu đăng ký thất bại
                    Toast.makeText(getApplication(), "Signup Error: $error", Toast.LENGTH_SHORT).show() // Hiển thị thông báo lỗi
                } else { // Nếu đăng ký thành công
                    Toast.makeText(getApplication(), "Please check your email for verification", Toast.LENGTH_SHORT).show() // Hiển thị thông báo yêu cầu xác minh email
                }
            }
        }
    }

    // Hàm kiểm tra tính hợp lệ của đầu vào
    private fun validateInputs(): String? {
        val emailValue = email.value.trim() // Lấy giá trị email và loại bỏ khoảng trắng
        val passwordValue = password.value // Lấy giá trị mật khẩu
        val confirmPasswordValue = confirmPassword.value // Lấy giá trị xác nhận mật khẩu
        val nameValue = name.value.trim() // Lấy giá trị tên và loại bỏ khoảng trắng
        val phoneValue = phone.value.trim() // Lấy giá trị số điện thoại và loại bỏ khoảng trắng

        if (emailValue.isEmpty()){ // Nếu email trống
            _emailError.value = true // Cập nhật trạng thái lỗi email
            return "Vui lòng nhập email" // Trả về thông báo lỗi
        }
        else{
            _emailError.value = false // Cập nhật trạng thái không lỗi email
        }

        if (passwordValue.isEmpty()){ // Nếu mật khẩu trống
            _passwordError.value = true // Cập nhật trạng thái lỗi mật khẩu
            return "Vui lòng nhập mật khẩu" // Trả về thông báo lỗi
        }
        else{
            _passwordError.value = false // Cập nhật trạng thái không lỗi mật khẩu
        }

        if (passwordValue.length < 6){ // Nếu mật khẩu ngắn hơn 6 ký tự
            _passwordError.value = true // Cập nhật trạng thái lỗi mật khẩu
            return "Mật khẩu phải có ít nhất 6 ký tự" // Trả về thông báo lỗi
        }
        else{
            _passwordError.value = false // Cập nhật trạng thái không lỗi mật khẩu
        }

        if (passwordValue.contains(emailValue)){ // Nếu mật khẩu chứa email
            _passwordError.value = true // Cập nhật trạng thái lỗi mật khẩu
            return "Mật khẩu không được chứa email" // Trả về thông báo lỗi
        }
        else{
            _passwordError.value = false // Cập nhật trạng thái không lỗi mật khẩu
        }

        if (passwordValue.contains(" ")){ // Nếu mật khẩu chứa khoảng trắng
            _passwordError.value = true // Cập nhật trạng thái lỗi mật khẩu
            return "Mật khẩu không được chứa khoảng trắng" // Trả về thông báo lỗi
        }
        else{
            _passwordError.value = false // Cập nhật trạng thái không lỗi mật khẩu
        }

        if (confirmPasswordValue.isEmpty()){ // Nếu xác nhận mật khẩu trống
            _confirmPasswordError.value = true // Cập nhật trạng thái lỗi xác nhận mật khẩu
            return "Vui lòng xác nhận mật khẩu" // Trả về thông báo lỗi
        }
        else{
            _confirmPasswordError.value = false // Cập nhật trạng thái không lỗi xác nhận mật khẩu
        }

        if (confirmPasswordValue != passwordValue){ // Nếu xác nhận mật khẩu không khớp với mật khẩu
            _confirmPasswordError.value = true // Cập nhật trạng thái lỗi xác nhận mật khẩu
            return "Mật khẩu xác nhận không khớp" // Trả về thông báo lỗi
        }
        else{
            _confirmPasswordError.value = false // Cập nhật trạng thái không lỗi xác nhận mật khẩu
        }
        if (!(emailValue.endsWith("@tlu.edu.vn") || emailValue.endsWith("@e.tlu.edu.vn"))){ // Nếu email không kết thúc bằng @tlu.edu.vn hoặc @e.tlu.edu.vn
            if (nameValue.isEmpty()){ // Nếu tên trống
                _nameError.value = true // Cập nhật trạng thái lỗi tên
                return "Vui lòng nhập tên" // Trả về thông báo lỗi
            }
            else{
                _nameError.value = false // Cập nhật trạng thái không lỗi tên
            }

            if (phoneValue.isEmpty()){ // Nếu số điện thoại trống
                _phoneError.value = true // Cập nhật trạng thái lỗi số điện thoại
                return "Vui lòng nhập số điện thoại" // Trả về thông báo lỗi
            }
            else{
                _phoneError.value = false // Cập nhật trạng thái không lỗi số điện thoại
            }

            if (phoneValue.length < 10){ // Nếu số điện thoại ngắn hơn 10 ký tự
                _phoneError.value = true // Cập nhật trạng thái lỗi số điện thoại
                return "Số điện thoại không hợp lệ" // Trả về thông báo lỗi
            }
            else{
                _phoneError.value = false // Cập nhật trạng thái không lỗi số điện thoại
            }

            if (!phoneValue.startsWith("0")){ // Nếu số điện thoại không bắt đầu bằng số 0
                _phoneError.value = true // Cập nhật trạng thái lỗi số điện thoại
                return "Số điện thoại không hợp lệ" // Trả về thông báo lỗi
            }
            else{
                _phoneError.value = false // Cập nhật trạng thái không lỗi số điện thoại
            }
        }
        return null // Trả về null nếu không có lỗi
    }

    // Hàm xử lý thay đổi giá trị tên
    fun onNameChange(newName: String) {
        state["name"] = newName // Cập nhật trạng thái tên
    }

    // Hàm xử lý thay đổi giá trị số điện thoại
    fun onPhoneChange(newPhone: String) {
        state["phone"] = newPhone // Cập nhật trạng thái số điện thoại
    }

    // Hàm xử lý thay đổi giá trị email
    fun onEmailChange(newEmail: String) {
        state["email"] = newEmail // Cập nhật trạng thái email
        _isEmailInvalid.value = !newEmail.endsWith("@tlu.edu.vn") && !newEmail.endsWith("@e.tlu.edu.vn") // Kiểm tra email hợp lệ
    }

    // Hàm xử lý thay đổi giá trị mật khẩu
    fun onPasswordChange(newPassword: String) {
        state["password"] = newPassword // Cập nhật trạng thái mật khẩu
    }

    // Hàm xử lý thay đổi giá trị xác nhận mật khẩu
    fun onConfirmPasswordChange(newConfirmPassword: String) {
        state["confirmPassword"] = newConfirmPassword // Cập nhật trạng thái xác nhận mật khẩu
    }
}