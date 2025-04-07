package com.example.tlucontact.viewmodel

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier

class StaffViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _staffList = MutableStateFlow<List<Staff>>(emptyList())
    val staffList: StateFlow<List<Staff>> = _staffList

    private val _selectedStaff = MutableStateFlow<Staff?>(null)
    val selectedStaff: StateFlow<Staff?> = _selectedStaff


    init {
        fetchStaffs() // Lấy tất cả nhân viên ban đầu
    }

    private fun fetchStaffs() {
        db.collection("staffs").get()
            .addOnSuccessListener { result ->
                val staffItems = result.map { doc ->
                    Staff(
                        staffId = doc.getString("staffid") ?: "",
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

    // Phương thức lọc nhân viên theo department (Khoa, Viện, Phòng, Trung tâm)
    fun filterStaffs(department: String) {
        db.collection("staffs")
            .whereEqualTo("unit", department) // Lọc theo department (Khoa, Viện...)
            .get()
            .addOnSuccessListener { result ->
                val staffItems = result.map { doc ->
                    Staff(
                        staffId = doc.id,
                        name = doc.getString("fullName") ?: "Không có tên",
                        email = doc.getString("email") ?: "",
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
}
