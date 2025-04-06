package com.example.tlucontact.data.model

data class Guest (
    override val email : String = "",
    override val phone : String = "",
    override val name : String = "",
    override val uid : String = "",
    val userType : String = "",
): User(email, phone, name, uid)