package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.repository.DepartmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted

class DepartmentViewModel(private val repository: DepartmentRepository) : ViewModel() {
    private val _departmentList = MutableStateFlow<List<Department>>(emptyList())
    val departmentList: StateFlow<List<Department>> = _departmentList

    private val _filterType = MutableStateFlow("")
    val filterType: StateFlow<String> = _filterType

    private val _sortAscending = MutableStateFlow(true) // Thêm trạng thái sắp xếp
    val sortAscending: StateFlow<Boolean> = _sortAscending

    val filteredDepartments: StateFlow<List<Department>> = combine(departmentList, filterType) { departments, type ->
        if (type.isEmpty()) {
            departments // Trả về danh sách gốc nếu không có lọc
        } else {
            departments.filter { it.type == type } // Lọc theo type
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        fetchDepartments()
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            try {
                _departmentList.value = repository.getDepartments()
            } catch (e: Exception) {
                // Xử lý lỗi
                println("Lỗi lấy dữ liệu: ${e.message}")
            }
        }
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun toggleSortOrder() { // Thêm hàm để thay đổi trạng thái sắp xếp
        _sortAscending.value = !_sortAscending.value
    }
}