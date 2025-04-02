package com.example.tlucontact.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tlucontact.data.model.Student
import com.example.tlucontact.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentDetailViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> get() = _student

    fun fetchStudent(studentId: String) {
        viewModelScope.launch {
            _student.value = repository.getStudentById(studentId)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }
}
