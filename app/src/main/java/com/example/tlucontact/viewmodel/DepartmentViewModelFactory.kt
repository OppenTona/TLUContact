package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tlucontact.data.repository.DepartmentRepository

class DepartmentViewModelFactory(private val repository: DepartmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DepartmentViewModel::class.java)) {
            DepartmentViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
