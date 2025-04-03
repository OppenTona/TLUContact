package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.repository.DepartmentRepository

class DepartmentViewModel(private val repository: DepartmentRepository) : ViewModel() {
    private val _departmentList = MutableStateFlow<List<Department>>(emptyList())
    val departmentList: StateFlow<List<Department>> = _departmentList

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
}
