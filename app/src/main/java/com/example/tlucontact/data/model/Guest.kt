package com.example.tlucontact.data.model

data class Guest (
    override val email : String = "",
    override val phone : String = "",
    val name : String = "",
    val userType : String = "",
): User(email, phone)