package com.example.tlucontact.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DirectoryViewModel : ViewModel() {
    private var _selectedTab = mutableStateOf("Giảng viên")
    val selectedTab: State<String> get() = _selectedTab

    fun setSelectTab(tab: String) {
        _selectedTab.value = tab
    }

}