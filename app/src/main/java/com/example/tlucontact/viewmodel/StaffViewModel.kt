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
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch


class StaffViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _staffList = MutableStateFlow<List<Staff>>(emptyList())
    val staffList: StateFlow<List<Staff>> = _staffList

    private val _selectedStaff = MutableStateFlow<Staff?>(null)
    val selectedStaff: StateFlow<Staff?> = _selectedStaff

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    private val _isUpdateSuccessful = MutableStateFlow(false)
    val isUpdateSuccessful: StateFlow<Boolean> = _isUpdateSuccessful

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending

    private val _filterMode = MutableStateFlow("All") // ByAll, ByDepartment, ByPosition
    val filterMode: StateFlow<String> = _filterMode

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
        fetchStaffs(query = null) // Làm mới danh sách khi thay đổi sắp xếp
    }

    init {
        fetchStaffs(query = null)
    }

    fun setFilterMode(mode: String) {
        _filterMode.value = mode
        fetchStaffs(query = null) // Làm mới danh sách khi đổi filter
    }

    /**
     * Truy vấn Firestore với các tham số lọc/sắp xếp
     */
    fun fetchStaffs(query: String?) {
        viewModelScope.launch {
            var ref: Query = db.collection("staffs")

            // Sắp xếp theo tên
            ref = if (_sortAscending.value) {
                ref.orderBy("fullName")
            } else {
                ref.orderBy("fullName", Query.Direction.DESCENDING)
            }

            // Áp dụng lọc nếu có
            when (_filterMode.value) {
                "ByDepartment" -> {
                    if (!query.isNullOrBlank()) {
                        ref = ref.whereGreaterThanOrEqualTo("unit", query)
                            .whereLessThanOrEqualTo("unit", query + '\uf8ff')
                    }
                }
                "ByPosition" -> {
                    if (!query.isNullOrBlank()) {
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

            // Giới hạn kết quả (ví dụ: 100)
            ref = ref.limit(100)

            ref.get()
                .addOnSuccessListener { result ->
                    val staffItems = result.map { doc ->
                        Staff(
                            staffId = doc.getString("staffid") ?: "",
                            staffIdFB = doc.getString("staffid") ?: "",
                            name = doc.getString("fullName") ?: "Không có tên", // <- thêm dòng nàyname = doc.getString("fullName") ?: "Không có tên", // <- thêm dòng này
                            email = doc.id,
                            phone = doc.getString("phone") ?: "",
                            department = doc.getString("unit") ?: "",
                            position = doc.getString("position") ?: "",
                            avatarURL = doc.getString("photoURL") ?: ""
                        )
                    }
                    _staffList.value = staffItems
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Lỗi lấy danh sách giảng viên: ${e.message}")
                }
        }
    }

    fun setStaffByEmail(emailUser: String) {
        db.collection("staffs").document(emailUser).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _selectedStaff.value = Staff(
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
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi lấy giảng viên: ${e.message}")
            }
    }

    fun updateStaffInfo(updatedStaff: Staff) {
        db.collection("staffs").document(updatedStaff.staffId)
            .set(
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
            .addOnSuccessListener {
                _selectedStaff.value = updatedStaff
                _updateMessage.value = "Cập nhật thông tin thành công"
                _isUpdateSuccessful.value = true
            }
            .addOnFailureListener { e ->
                _updateMessage.value = "Lỗi cập nhật: ${e.message}"
                _isUpdateSuccessful.value = false
            }
    }

    fun clearUpdateMessage() {
        _updateMessage.value = null
        _isUpdateSuccessful.value = false
    }

    fun uploadImageToStorage(
        uri: Uri?,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (uri == null) {
            onFailure(IllegalArgumentException("Uri ảnh không được null"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("avatars/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }.addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }
}


