package com.example.tlucontact.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tlucontact.data.model.Staff
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier
import java.util.UUID


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

    // Trạng thái sắp xếp: true = A-Z, false = Z-A
    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending

    init {
        fetchStaffs()
    }

    private fun fetchStaffs() {
        db.collection("staffs").get()
            .addOnSuccessListener { result ->
                val staffItems = result.map { doc ->
                    Staff(
                        staffId = doc.getString("staffid") ?: "",
                        staffIdFB = doc.getString("staffid") ?: "",
                        name = doc.getString("fullName") ?: "Không có tên",
                        email = doc.id,
                        phone = doc.getString("phone") ?: "",
                        department = doc.getString("unit") ?: "",
                        position = doc.getString("position") ?: "",
                        avatarURL = doc.getString("photoURL") ?: ""
                    )
                }
                _staffList.value = staffItems
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
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
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
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
            .addOnFailureListener { exception ->
                _updateMessage.value = "Lỗi cập nhật: ${exception.message}"
                _isUpdateSuccessful.value = false
            }
    }

    fun clearUpdateMessage() {
        _updateMessage.value = null
        _isUpdateSuccessful.value = false
    }

    // Toggle thứ tự sắp xếp
    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
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

        Log.d("UploadImage", "Bắt đầu upload ảnh: $uri")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    Log.d("UploadImage", "Upload thành công. URL: $downloadUrl")
                    onSuccess(downloadUrl.toString())
                }.addOnFailureListener { e ->
                    Log.e("UploadImage", "Lấy URL thất bại: ${e.message}")
                    onFailure(e)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UploadImage", "Upload ảnh thất bại: ${exception.message}")
                onFailure(exception)
            }

    }




}

