package com.example.tlucontact.data.model

data class Guest (
    override val email : String = "",
    override val phone : String = "",
    override val name : String = "",
    override val uid : String = "",
    val avatarURL : String = "",
    val department: String = "",
    val position: String = "",
    val address: String = "",
    val userType : String = "",
    val userId : String = "",
): User(email, phone, name, uid)