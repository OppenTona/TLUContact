package com.example.tlucontact.data.model

data class Student(
    val studentID : String = "",
    val fullNameStudent : String = "",
    val photoURL : String = "",
    override val email : String = "",
    override val phone : String = "",
    val address : String = "",
    val className : String = "",
    val userID : String = ""
) : User(email, phone)