package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Department
import com.example.tlucontact.data.repository.DepartmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DepartmentViewModel(private val repository: DepartmentRepository) : ViewModel() {

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments

    init {
        fetchDepartments()
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            try {
                val departmentList = repository.getDepartments()
                _departments.value = departmentList
            } catch (e: Exception) {
                _departments.value = emptyList()
            }
        }
    }
}
