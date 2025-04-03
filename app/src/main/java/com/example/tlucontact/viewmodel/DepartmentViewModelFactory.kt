package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tlucontact.data.repository.DepartmentRepository

class DepartmentViewModelFactory(private val repository: DepartmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DepartmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DepartmentViewModel(repository) as T // Truyền repository vào constructor
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}