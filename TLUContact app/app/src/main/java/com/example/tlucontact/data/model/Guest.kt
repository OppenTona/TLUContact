package com.example.tlucontact.data.model // Định nghĩa package chứa lớp Guest

// Định nghĩa lớp dữ liệu Guest kế thừa từ lớp User
data class Guest (
    override val email : String = "", // Thuộc tính email, kế thừa từ lớp User, mặc định là chuỗi rỗng
    override val phone : String = "", // Thuộc tính phone, kế thừa từ lớp User, mặc định là chuỗi rỗng
    override val name : String = "", // Thuộc tính name, kế thừa từ lớp User, mặc định là chuỗi rỗng
    override val uid : String = "", // Thuộc tính uid, kế thừa từ lớp User, mặc định là chuỗi rỗng
    val avatarURL : String = "", // Thuộc tính avatarURL, lưu URL của ảnh đại diện, mặc định là chuỗi rỗng
    val department: String = "", // Thuộc tính department, lưu thông tin phòng ban, mặc định là chuỗi rỗng
    val position: String = "", // Thuộc tính position, lưu thông tin chức vụ, mặc định là chuỗi rỗng
    val address: String = "", // Thuộc tính address, lưu thông tin địa chỉ, mặc định là chuỗi rỗng
    val userType : String = "", // Thuộc tính userType, lưu loại người dùng (ví dụ: guest), mặc định là chuỗi rỗng
    val userId : String = "", // Thuộc tính userId, lưu mã định danh người dùng, mặc định là chuỗi rỗng
): User(email, phone, name, uid) // Kế thừa từ lớp User và truyền các thuộc tính kế thừa vào constructor của lớp cha