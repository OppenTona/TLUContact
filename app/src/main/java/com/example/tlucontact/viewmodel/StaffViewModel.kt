package com.example.tlucontact.viewmodel
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tlucontact.data.model.Staff
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class StaffViewModel : ViewModel() { // Lớp ViewModel quản lý dữ liệu và logic liên quan đến giảng viên
    private val db = FirebaseFirestore.getInstance() // Tạo một instance Firestore để tương tác với cơ sở dữ liệu

    private val _staffList = MutableStateFlow<List<Staff>>(emptyList()) // Biến nội bộ lưu danh sách giảng viên
    val staffList: StateFlow<List<Staff>> = _staffList // Biến public để UI quan sát danh sách giảng viên

    private val _selectedStaff = MutableStateFlow<Staff?>(null) // Biến nội bộ lưu giảng viên được chọn
    val selectedStaff: StateFlow<Staff?> = _selectedStaff // Biến public để UI quan sát giảng viên đang được chọn

    private val _updateMessage = MutableStateFlow<String?>(null) // Biến nội bộ chứa thông điệp cập nhật
    val updateMessage: StateFlow<String?> = _updateMessage // Biến public để UI quan sát thông điệp

    private val _isUpdateSuccessful = MutableStateFlow(false) // Biến nội bộ lưu trạng thái cập nhật thành công hay không
    val isUpdateSuccessful: StateFlow<Boolean> = _isUpdateSuccessful // Biến public để UI quan sát trạng thái

    private val _sortAscending = MutableStateFlow(true) // True: A-Z, False: Z-A
    val sortAscending: StateFlow<Boolean> = _sortAscending
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }
    init {
        fetchStaffs() // Gọi hàm fetchStaffs khi ViewModel được khởi tạo
    }

    private fun fetchStaffs() { // Hàm lấy toàn bộ danh sách giảng viên từ Firestore
        db.collection("staffs").get() // Lấy toàn bộ document trong collection "staffs"
            .addOnSuccessListener { result -> // Nếu thành công thì...
                val staffItems = result.map { doc -> // Duyệt qua từng document và ánh xạ thành đối tượng Staff
                    Staff(
                        staffId = doc.getString("staffid") ?: "", // Lấy mã nhân viên (nếu null thì để trống)
                        staffIdFB = doc.getString("staffid") ?: "", // Trùng với staffId
                        name = doc.getString("fullName") ?: "Không có tên", // Lấy tên đầy đủ, fallback nếu null
                        email = doc.id, // Email chính là document ID
                        phone = doc.getString("phone") ?: "", // Lấy số điện thoại
                        department = doc.getString("unit") ?: "", // Lấy đơn vị công tác
                        position = doc.getString("position") ?: "", // Lấy chức vụ
                        avatarURL = doc.getString("photoURL") ?: "" // Lấy URL ảnh đại diện
                    )
                }
                _staffList.value = staffItems // Cập nhật danh sách giảng viên vào biến StateFlow
            }
            .addOnFailureListener { exception -> // Nếu thất bại thì in lỗi ra log
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }

    fun setStaffByEmail(emailUser: String) { // Hàm lấy thông tin một giảng viên theo email
        db.collection("staffs").document(emailUser).get() // Lấy document theo email
            .addOnSuccessListener { doc -> // Nếu thành công
                if (doc.exists()) { // Nếu document tồn tại
                    _selectedStaff.value = Staff( // Tạo đối tượng Staff và gán vào selectedStaff
                        staffId = doc.id,
                        name = doc.getString("fullName") ?: "Không có tên",
                        staffIdFB = doc.getString("staffid") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        department = doc.getString("unit") ?: "",
                        position = doc.getString("position") ?: "",
                        avatarURL = doc.getString("photoURL") ?: ""
                    )
                }
            }
            .addOnFailureListener { exception -> // Nếu thất bại thì in lỗi
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }

    fun updateStaffInfo(updatedStaff: Staff) { // Hàm cập nhật thông tin của một giảng viên
        db.collection("staffs").document(updatedStaff.staffId) // Truy cập document theo ID
            .set( // Ghi đè dữ liệu trong document bằng map dưới đây
                mapOf(
                    "fullName" to updatedStaff.name,
                    "phone" to updatedStaff.phone,
                    "unit" to updatedStaff.department,
                    "position" to updatedStaff.position,
                    "staffid" to updatedStaff.staffIdFB,
                    "userId" to updatedStaff.userId,
                    "photoURL" to updatedStaff.avatarURL
                )
            )
            .addOnSuccessListener { // Nếu cập nhật thành công
                _selectedStaff.value = updatedStaff // Gán lại selectedStaff
                _updateMessage.value = "Cập nhật thông tin thành công" // Thông điệp báo thành công
                _isUpdateSuccessful.value = true // Đánh dấu là đã cập nhật thành công
            }
            .addOnFailureListener { exception -> // Nếu cập nhật thất bại
                _updateMessage.value = "Lỗi cập nhật: ${exception.message}" // Ghi thông điệp lỗi
                _isUpdateSuccessful.value = false // Đánh dấu là thất bại
            }
    }

    fun clearUpdateMessage() { // Hàm xóa thông điệp cập nhật để tránh hiện thông báo cũ
        _updateMessage.value = null
        _isUpdateSuccessful.value = false
    }
    private val _filterMode = MutableStateFlow("All")
    val filterMode: StateFlow<String> = _filterMode

    fun setFilterMode(mode: String) {
        _filterMode.value = mode
    }


    fun uploadImageToStorage( // Hàm upload ảnh lên Firebase Storage
        uri: Uri?, // Uri của ảnh
        onSuccess: (String) -> Unit, // Callback khi thành công (trả về URL)
        onFailure: (Exception) -> Unit // Callback khi thất bại
    ) {
        if (uri == null) { // Nếu uri null thì trả lỗi luôn
            onFailure(IllegalArgumentException("Uri ảnh không được null"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference // Tham chiếu đến Firebase Storage gốc
        val imageRef = storageRef.child("avatars/${UUID.randomUUID()}.jpg") // Tạo tên file ngẫu nhiên trong thư mục "avatars"

        Log.d("UploadImage", "Bắt đầu upload ảnh: $uri") // In log để theo dõi

        imageRef.putFile(uri) // Upload file lên Storage
            .addOnSuccessListener { // Nếu upload thành công
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl -> // Lấy URL của ảnh đã upload
                    Log.d("UploadImage", "Upload thành công. URL: $downloadUrl")
                    onSuccess(downloadUrl.toString()) // Gọi callback thành công và trả URL
                }.addOnFailureListener { e -> // Nếu lấy URL thất bại
                    Log.e("UploadImage", "Lấy URL thất bại: ${e.message}")
                    onFailure(e)
                }
            }
            .addOnFailureListener { exception -> // Nếu upload ảnh thất bại
                Log.e("UploadImage", "Upload ảnh thất bại: ${exception.message}")
                onFailure(exception)
            }
    }
}


