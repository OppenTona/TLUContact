package com.example.tlucontact.viewmodel

// Import các thư viện cần thiết
import android.net.Uri // Thư viện Android dùng để xử lý các đường dẫn URI (ảnh, video, file...)
import android.util.Log // Dùng để ghi log ra Logcat (Log.d, Log.e...) phục vụ debug
import androidx.lifecycle.ViewModel // ViewModel trong Jetpack, quản lý dữ liệu UI an toàn với vòng đời
import androidx.lifecycle.viewModelScope // Coroutine scope đi kèm ViewModel để xử lý bất đồng bộ an toàn
import com.example.tlucontact.data.model.Staff // Model Staff do bạn định nghĩa, chứa thông tin giảng viên
import com.google.firebase.firestore.FirebaseFirestore // Firebase Firestore – CSDL NoSQL lưu trữ dữ liệu như danh sách giảng viên
import com.google.firebase.firestore.Query // Dùng để tạo truy vấn nâng cao trên Firestore (lọc, sắp xếp, tìm kiếm)
import com.google.firebase.storage.FirebaseStorage // Firebase Storage – lưu trữ tệp như ảnh đại diện giảng viên
import kotlinx.coroutines.flow.MutableStateFlow // StateFlow có thể thay đổi – dùng trong ViewModel để phát dữ liệu ra UI
import kotlinx.coroutines.flow.StateFlow // Phiên bản chỉ đọc của MutableStateFlow – dùng cho UI quan sát
import kotlinx.coroutines.launch // Hàm để khởi chạy coroutine trong scope (như viewModelScope)
import java.util.UUID // Tạo ID ngẫu nhiên – ví dụ: tên file ảnh khi upload lên Firebase Storage


// Định nghĩa lớp ViewModel quản lý dữ liệu Staff
class StaffViewModel : ViewModel() {

    // Khởi tạo instance của Firestore
    private val db = FirebaseFirestore.getInstance()

    // StateFlow để chứa danh sách giảng viên, ban đầu là rỗng
    private val _staffList = MutableStateFlow<List<Staff>>(emptyList())
    val staffList: StateFlow<List<Staff>> = _staffList // Expose ra ngoài dưới dạng StateFlow không thể thay đổi
    //StateFlow luôn giữ trạng thái hiện tại và thông báo cho UI mỗi khi giá trị thay đổi.


    // StateFlow cho giảng viên đang được chọn
    private val _selectedStaff = MutableStateFlow<Staff?>(null) //_ là biến nội bộ (internal), không thể truy cập từ bên ngoài
    val selectedStaff: StateFlow<Staff?> = _selectedStaff //? tức là có thể null hoặc là 1 staff

    // Thông điệp hiển thị sau khi cập nhật thông tin
    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    // Trạng thái cập nhật thành công hay thất bại
    private val _isUpdateSuccessful = MutableStateFlow(false)

    // Trạng thái sắp xếp tăng dần hay giảm dần theo tên
    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending

    // Trạng thái lọc: All, ByDepartment, ByPosition
    private val _filterMode = MutableStateFlow("All")
    val filterMode: StateFlow<String> = _filterMode

    // Hàm đảo ngược trạng thái sắp xếp, đồng thời làm mới danh sách
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value //đảo ngược giá trị hiện tại
        fetchStaffs(query = null) // gọi lại hàm fetchStaffs để cập nhật danh sách
    }

    // Gọi khi khởi tạo ViewModel, để tự động fetch danh sách
    init {
        fetchStaffs(query = null) // Gọi hàm fetchStaffs để lấy danh sách giảng viên từ Firestore
    }

    // Cập nhật chế độ lọc và làm mới danh sách
    fun setFilterMode(mode: String) { // mode là kiểu lọc: All, ByDepartment, ByPosition
        _filterMode.value = mode // cập nhật chế độ lọc
        fetchStaffs(query = null)  // gọi lại hàm fetchStaffs để cập nhật danh sách
    }

    // Hàm lấy danh sách giảng viên từ Firestore, có hỗ trợ lọc và sắp xếp
    fun fetchStaffs(query: String?) { // query là từ khóa tìm kiếm (? là có thể null)
        viewModelScope.launch { // Khởi chạy coroutine trong viewModelScope // Dễ xử lý bất đồng bộ để tải dữ liệu Firestore hoặc gọi API...
            //giúp chạy các coroutine bất đồng bộ trong ViewModel một cách dễ dàng và an toàn mà không phải lo lắng về việc hủy chúng khi ViewModel không còn tồn tại.
            // Kiểm tra xem có kết nối Internet không
            var ref: Query = db.collection("staffs") // Truy cập collection "staffs"

            // Thêm điều kiện sắp xếp theo tên
            ref = if (_sortAscending.value) { // Nếu đang sắp xếp tăng dần
                ref.orderBy("fullName") // Sắp xếp theo tên giảng viên
            } else {  // Nếu đang sắp xếp giảm dần
                ref.orderBy("fullName", Query.Direction.DESCENDING) // Sắp xếp theo tên giảng viên giảm dần
            }

            // Áp dụng điều kiện lọc theo chế độ
            when (_filterMode.value) { // Kiểu lọc: All/ByDepartment/ByPosition
                "ByDepartment" -> { // Lọc theo đơn vị
                    if (!query.isNullOrBlank()) { // Nếu query không null hoặc rỗng
                        ref = ref.whereGreaterThanOrEqualTo("unit", query) // tìm tương đối
                            .whereLessThanOrEqualTo("unit", query + '\uf8ff') // tìm tương đối
                    } // Nếu không có query, sẽ lấy tất cả giảng viên
                }
                // Lọc theo vị trí
                "ByPosition" -> { // Lọc theo vị trí
                    if (!query.isNullOrBlank()) { // Nếu query không null hoặc rỗng
                        ref = ref.whereGreaterThanOrEqualTo("position", query)
                            .whereLessThanOrEqualTo("position", query + '\uf8ff')
                    }
                }
                "ByAll" -> {
                    if (!query.isNullOrBlank()) {
                        ref = ref.whereGreaterThanOrEqualTo("fullName", query)
                            .whereLessThanOrEqualTo("fullName", query + '\uf8ff')
                    }
                }
            }

            // Giới hạn số kết quả trả về
            ref = ref.limit(100) // Giới hạn số lượng kết quả trả về tối đa là 100

            // Lấy dữ liệu từ Firestore
            ref.get() // Trả về một Task chứa kết quả
                .addOnSuccessListener { result -> // Khi lấy dữ liệu thành công
                    // Chuyển dữ liệu từ Firestore thành danh sách Staff
                    val staffItems = result.map { doc -> // Chuyển đổi từng document thành Staff
                        Staff( // Khởi tạo đối tượng Staff
                            staffId = doc.getString("staffid") ?: "",
                            staffIdFB = doc.getString("staffid") ?: "", // Lấy staffId từ document
                            name = doc.getString("fullName") ?: "Không có tên", // Lấy tên từ document
                            email = doc.id, // Lấy email từ document ID
                            phone = doc.getString("phone") ?: "", // Lấy số điện thoại từ document
                            department = doc.getString("unit") ?: "", // Lấy đơn vị từ document
                            position = doc.getString("position") ?: "", // Lấy vị trí từ document
                            avatarURL = doc.getString("photoURL") ?: "" // Lấy URL ảnh từ document
                        )
                    }
                    _staffList.value = staffItems // Cập nhật StateFlow
                }
                .addOnFailureListener { e -> // Khi có lỗi xảy ra
                    Log.e("Firestore", "Lỗi lấy danh sách giảng viên: ${e.message}") // Ghi log lỗi để dễ kiểm tra
                }
        }
    }

    // Hàm tìm giảng viên theo email (dùng khi vào trang chỉnh sửa)
    fun setStaffByEmail(emailUser: String) { // emailUser là email của giảng viên
        db.collection("staffs").document(emailUser).get() // Lấy document theo email
            .addOnSuccessListener { doc -> // Khi lấy dữ liệu thành công
                if (doc.exists()) { // Kiểm tra xem document có tồn tại không
                    _selectedStaff.value = Staff( // Khởi tạo đối tượng Staff
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
            .addOnFailureListener { e -> // Khi có lỗi xảy ra
                Log.e("Firestore", "Lỗi lấy giảng viên: ${e.message}") // Ghi log lỗi để dễ kiểm tra
            }
    }

    // Cập nhật thông tin giảng viên lên Firestore
    fun updateStaffInfo(updatedStaff: Staff) { // updatedStaff là đối tượng Staff đã được chỉnh sửa
        db.collection("staffs").document(updatedStaff.staffId) // Truy cập document theo staffId
            .set( // Cập nhật dữ liệu trong document
                mapOf( // Sử dụng map để truyền dữ liệu
                    "fullName" to updatedStaff.name, // Tên giảng viên
                    "phone" to updatedStaff.phone, // Số điện thoại
                    "unit" to updatedStaff.department,  // Đơn vị
                    "position" to updatedStaff.position, // Vị trí
                    "staffid" to updatedStaff.staffIdFB, // ID giảng viên
                    "userId" to updatedStaff.userId, // ID người dùng
                    "photoURL" to updatedStaff.avatarURL // URL ảnh đại diện
                )
            )
            .addOnSuccessListener { // Khi cập nhật thành công
                _selectedStaff.value = updatedStaff // Cập nhật dữ liệu trong ViewModel
                _updateMessage.value = "Cập nhật thông tin thành công" // Thông báo cập nhật thành công
                _isUpdateSuccessful.value = true // Đánh dấu cập nhật thành công
            }
            .addOnFailureListener { e -> // Khi có lỗi xảy ra
                _updateMessage.value = "Lỗi cập nhật: ${e.message}" // Thông báo lỗi
                _isUpdateSuccessful.value = false // Đánh dấu cập nhật thất bại
            }
    }

    // Xoá message và trạng thái cập nhật (gọi sau khi hiển thị)
    fun clearUpdateMessage() { // Hàm này được gọi sau khi hiển thị thông báo cập nhật
        _updateMessage.value = null // Xoá thông báo
        _isUpdateSuccessful.value = false // Đánh dấu cập nhật chưa thành công
    }

    // Upload ảnh avatar lên Firebase Storage, gọi callback khi thành công hoặc lỗi
    fun uploadImageToStorage( // Hàm này dùng để upload ảnh lên Firebase Storage
        uri: Uri?, // Uri của ảnh cần upload
        onSuccess: (String) -> Unit, // Callback khi upload thành công, trả về URL ảnh
        onFailure: (Exception) -> Unit
    ) {
        if (uri == null) {
            onFailure(IllegalArgumentException("Uri ảnh không được null"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("avatars/${UUID.randomUUID()}.jpg") // Đặt tên file ngẫu nhiên

        imageRef.putFile(uri) // Upload ảnh lên Firebase, uri là kiểu dữ liệu cho phép lưu đường dẫn đến ảnh
            .addOnSuccessListener { // Khi upload thành công
                imageRef.downloadUrl.addOnSuccessListener { url -> // Lấy URL của ảnh vừa upload
                    onSuccess(url.toString()) // Trả về đường dẫn URL khi thành công
                }.addOnFailureListener { onFailure(it) } // Nếu lấy URL thất bại
            }
            .addOnFailureListener { onFailure(it) } // Nếu upload thất bại
    }
}
