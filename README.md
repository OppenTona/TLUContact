# TLUContact [English caption below]

Dự án bài tập lớn TLUContact của các sinh viên trường đại học Thủy lợi.

## Thành viên nhóm:

- Nguyễn Thế Toàn (OppenTona)
- Nguyễn Thị Thanh Vân (ThanhVan2024)
- Lưu Hiểu Khánh (HieuKhanh04)
- Lê Hà Phương (LeHaPhuong2004)

## Giới thiệu về đề tài

- **Hiện trạng:**
    - Việc quản lý thông tin liên lạc tại Trường Đại học Thủy Lợi chủ yếu dựa vào các tài liệu giấy hoặc file Word.
    - Điều này dẫn đến nhiều bất cập như khó khăn trong tìm kiếm, cập nhật chậm trễ và nguy cơ mất mát hoặc lọt dữ liệu.

- **Vấn đề:**
    - Chưa có hệ thống danh bạ tập trung giúp sinh viên và giảng viên liên lạc trực tiếp với nhau.
    - Việc trao đổi thông tin thường phải thông qua bên trung gian như giáo viên chủ nhiệm hoặc liên chi đoàn khoa gây mất thời gian và giảm hiệu quả giao tiếp.

- **Giải pháp:**
    - Dự án “TLUContact – Ứng dụng danh bạ điện tử cho Đại học Thủy Lợi” ra đời nhằm giải quyết các vấn đề trên.
    - Ứng dụng phát triển trên hệ điều hành Android, sử dụng ngôn ngữ lập trình Kotlin và cơ sở dữ liệu NoSQL của Firebase (Cloud Firestore).
    - Đảm bảo lưu trữ linh hoạt, truy xuất nhanh chóng và đồng bộ dữ liệu theo thời gian thực.
    - Tích hợp Firebase Authentication để hỗ trợ đăng ký, xác thực và phân quyền người dùng, đảm bảo tính bảo mật và riêng tư trong quá trình sử dụng.

- **Mục tiêu:**
    - TLUContact hướng đến trở thành cầu nối thông tin hiệu quả giữa các thành viên trong trường.
    - Giúp nâng cao hiệu suất làm việc và học tập.
    - Thúc đẩy quá trình số hóa trong giáo dục.

## Giao diện người dùng
https://www.figma.com/design/ezrsY5J8DKXXSKSgMs3o6s/Untitled?node-id=0-1&p=f&t=62QHq8iLZ3Ec66an-0

## Tính năng chính
- Thêm, sửa, xóa danh bạ
- Tìm kiếm và lọc danh bạ theo tên, số điện thoại
- Nhập và xuất danh bạ từ các định dạng phổ biến
- Đồng bộ hóa danh bạ với các dịch vụ đám mây
- Gửi tin nhắn và gọi điện trực tiếp từ ứng dụng

## Công nghệ sử dụng
- Ngôn ngữ lập trình: Kotlin
- Framework: Android SDK
- Cơ sở dữ liệu: SQLite
- Công cụ phát triển: Android Studio

## Cấu trúc dự án
```plaintext
TLUContact/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── oppentona/
│   │   │   │           └── tlucontact/
│   │   │   │               └── (các file mã nguồn Kotlin)
│   │   │   ├── res/
│   │   │   │   └── (các file tài nguyên giao diện người dùng)
│   │   │   ├── AndroidManifest.xml
│   │   └── test/
│   │       └── (các file kiểm thử)
│   └── build.gradle
├── build.gradle
└── settings.gradle
```


# TLUContact – English Version

A major coursework project developed by students from Thuyloi University.

## Team Members

Nguyễn Thế Toàn (OppenTona)

Nguyễn Thị Thanh Vân (ThanhVan2024)

Lưu Hiểu Khánh (HieuKhanh04)

Lê Hà Phương (LeHaPhuong2004)

## Project Introduction
- **Current Situation**

Contact information at Thuyloi University is mainly managed using paper documents or Word files.

This approach causes several challenges such as difficult searching, slow updates, and a high risk of data loss or leakage.

- **Problem Statement**
    - There is no centralized contact system that allows direct communication between students and lecturers.
    - Information exchange often requires intermediaries (e.g., homeroom teachers or youth union representatives), leading to delays and reduced communication efficiency.

- **Solution**
    - The project "TLUContact – Electronic Contact Directory for Thuyloi University" was created to solve these issues.
    - The application runs on Android, developed in Kotlin, and uses Firebase Cloud Firestore (NoSQL) for flexible storage, fast data retrieval, and real-time synchronization.
    - It also integrates Firebase Authentication to support user registration, verification, and role-based access control, ensuring security and privacy.

- **Objectives**
    - Become an effective communication bridge between all university members.
    - Improve productivity in studying and working.
    - Support the digital transformation process in education.

- **User Interface**
https://www.figma.com/design/ezrsY5J8DKXXSKSgMs3o6s/Untitled?node-id=0-1&p=f&t=62QHq8iLZ3Ec66an-0

## Key Features
- Add, edit, and delete contacts
- Search and filter contacts by name or phone number
- Import and export contacts from common formats
- Synchronize contacts with cloud services
- Send messages or make calls directly within the application

## Technologies Used
- Programming language: Kotlin
- Framework: Android SDK
- Database: SQLite
- Development tool: Android Studio
- Framework: Android SDK
- Database: SQLite
- Development tool: Android Studio
- Project Structure
```plaintext
TLUContact/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── oppentona/
│   │   │   │           └── tlucontact/
│   │   │   │               └── (các file mã nguồn Kotlin)
│   │   │   ├── res/
│   │   │   │   └── (các file tài nguyên giao diện người dùng)
│   │   │   ├── AndroidManifest.xml
│   │   └── test/
│   │       └── (các file kiểm thử)
│   └── build.gradle
├── build.gradle
└── settings.gradle
```
