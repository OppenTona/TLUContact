package com.example.tlucontact.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tlucontact.data.model.Department
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class DepartmentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _departmentList = MutableStateFlow<List<Department>>(emptyList())
    val departmentList: StateFlow<List<Department>> = _departmentList

    private val _selectedDepartment = MutableStateFlow<Department?>(null)
    val selectedDepartment: StateFlow<Department?> = _selectedDepartment

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending

    // Thêm StateFlow để lưu trữ danh sách đã lọc
    private val _filteredDepartmentList = MutableStateFlow<List<Department>>(emptyList())
    val filteredDepartmentList: StateFlow<List<Department>> = _filteredDepartmentList


    init {
        fetchDepartments()
    }

    private fun fetchDepartments() {
        db.collection("department").get()
            .addOnSuccessListener { result ->
                val departmentItems = result.map { doc ->
                    Department(
                        id = doc.getString("id") ?: "",
                        name = doc.getString("name") ?: "Không có tên",
                        leader = doc.getString("leader") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        photoURL = doc.getString("photoURL") ?: "",
                        type = doc.getString("type") ?: ""
                    )
                }
                _departmentList.value = departmentItems
                applyFilters() // Gọi applyFilters sau khi lấy dữ liệu ban đầu
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }

    fun setDepartmentById(departmentId: String) {
        db.collection("department").document(departmentId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _selectedDepartment.value = Department(
                        id = doc.id,
                        name = doc.getString("name") ?: "Không có tên",
                        leader = doc.getString("leader") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        photoURL = doc.getString("photoURL") ?: "",
                        type = doc.getString("type") ?: ""
                    )
                }
            }
            .addOnFailureListener { exception ->
                println("Lỗi lấy dữ liệu: ${exception.message}")
            }
    }

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }

    // In DepartmentViewModel class
    private val _filterMode = MutableStateFlow("All")
    val filterMode: StateFlow<String> = _filterMode

    fun setFilterMode(mode: String) {
        _filterMode.value = mode
        applyFilters()
    }

    fun applyFilters(query: String = "") {
        val filteredList = _departmentList.value.filter { department ->
            //Tim kiem theo ten va id
            val matchesQuery = department.name.contains(query, ignoreCase = true) || department.id.contains(query, ignoreCase = true)
            val matchesFilter = when (_filterMode.value) {
                "Khoa" -> department.type == "Khoa"
                "Phòng" -> department.type == "Phòng"
                "Trung tâm" -> department.type == "Trung tâm"
                "Viện" -> department.type == "Viện"
                else -> true
            }
            matchesQuery && matchesFilter
        }
        _filteredDepartmentList.value = filteredList
    }

    // Hàm mới để cập nhật danh sách khi query thay đổi
    fun setQuery(query: String) {
        applyFilters(query)
    }

}
