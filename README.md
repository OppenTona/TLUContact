# TLUContact – English Version

A major coursework project developed by students from Thuyloi University.

## Team Members

- Team Leader: Nguyễn Thế Toàn (OppenTona)
- Member: Nguyễn Thị Thanh Vân (ThanhVan2024)
- Member: Lưu Hiểu Khánh (HieuKhanh04)
- Member: Lê Hà Phương (LeHaPhuong2004)

## Project Introduction

- **Current Situation:**
  Contact information at Thuyloi University is mainly managed using paper documents or Word files.
  This approach causes several challenges such as difficult searching, slow updates, and a high risk of data loss or leakage.

- **Problem Statement:**
    - There is no centralized contact system that allows direct communication between students and lecturers.
    - Information exchange often requires intermediaries (e.g., homeroom teachers or youth union representatives), leading to delays and reduced communication efficiency.

- **Solution:**
    - The project "TLUContact – Electronic Contact Directory for Thuyloi University" was created to solve these issues.
    - The application runs on Android, developed in Kotlin with Jetpack Compose for modern UI design.
    - Uses Firebase Cloud Firestore (NoSQL) for flexible storage, fast data retrieval, and real-time synchronization.
    - Integrates Firebase Authentication for user registration, verification, and role-based access control, ensuring security and privacy.
    - Utilizes Firebase Functions for backend logic and Firebase Storage for file management.

- **Objectives:**
    - Become an effective communication bridge between all university members.
    - Improve productivity in studying and working.
    - Support the digital transformation process in education.

## User Interface Design
https://www.figma.com/design/ezrsY5J8DKXXSKSgMs3o6s/Untitled?node-id=0-1&p=f&t=62QHq8iLZ3Ec66an-0

## Key Features
- Add, edit, and delete contacts with Jetpack Compose UI
- Search and filter contacts by name or phone number
- Import and export contacts from common formats
- Synchronize contacts with Firebase Cloud Services
- Send messages and make calls directly within the application
- User authentication and access control

## Technologies Used
- **Programming Language:** Kotlin
- **Framework:** Android SDK (API 35)
- **UI Framework:** Jetpack Compose with Material Design 3
- **Database:** Firebase Cloud Firestore (NoSQL)
- **Authentication:** Firebase Authentication
- **Storage:** Firebase Storage
- **Backend:** Firebase Functions (Node.js 22)
- **Development Tool:** Android Studio
- **Build System:** Gradle Kotlin DSL

## System Requirements
- **Min SDK:** API 26 (Android 8.0)
- **Target SDK:** API 35 (Android 15)
- **Compile SDK:** API 35
- **Java Version:** 11

## Project Structure
```plaintext
TLUContact/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/tlucontact/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── view/
│   │   │   │   │   └── HomeScreen.kt
│   │   │   │   └── (other Kotlin source files)
│   │   │   ├── res/
│   │   │   │   └── (Compose UI resources)
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── (test files)
│   └── build.gradle.kts
├── functions/
│   ├── index.js
│   └── package.json (Node.js 22)
├── build.gradle.kts
└── settings.gradle.kts
```

## Installation and Setup

### Environment Requirements
- Android Studio Arctic Fox or newer
- JDK 11
- Configured Firebase project

### Setup Steps
1. Clone the repository:
```bash
git clone https://github.com/OppenTona/TLUContact.git
```

2. Open project in Android Studio

3. Configure Firebase:
   - Add `google-services.json` file to `app/` folder
   - Ensure Firebase Authentication, Firestore, and Storage are enabled

4. Sync project and build

5. Run the application on device or emulator

## Contributing
All contributions to improve the project are welcome. Please create issues or pull requests.

## License
This project is developed for educational purposes at Thuyloi University.

---

# TLUContact [Phiên bản tiếng Việt bên dưới]

Dự án bài tập lớn TLUContact của các sinh viên trường đại học Thủy lợi.

## Thành viên nhóm:

- Trưởng nhóm: Nguyễn Thế Toàn (OppenTona)
- Thành viên: Nguyễn Thị Thanh Vân (ThanhVan2024)
- Thành viên: Lưu Hiểu Khánh (HieuKhanh04)
- Thành viên: Lê Hà Phương (LeHaPhuong2004)

## Giới thiệu về đề tài

- **Hiện trạng:**
    - Việc quản lý thông tin liên lạc tại Trường Đại học Thủy Lợi chủ yếu dựa vào các tài liệu giấy hoặc file Word.
    - Điều này dẫn đến nhiều bất cập như khó khăn trong tìm kiếm, cập nhật chậm trễ và nguy cơ mất mát hoặc lọt dữ liệu.

- **Vấn đề:**
    - Chưa có hệ thống danh bạ tập trung giúp sinh viên và giảng viên liên lạc trực tiếp với nhau.
    - Việc trao đổi thông tin thường phải thông qua bên trung gian như giáo viên chủ nhiệm hoặc liên chi đoàn khoa gây mất thời gian và giảm hiệu quả giao tiếp.

- **Giải pháp:**
    - Dự án "TLUContact – Ứng dụng danh bạ điện tử cho Đại học Thủy Lợi" ra đời nhằm giải quyết các vấn đề trên.
    - Ứng dụng phát triển trên hệ điều hành Android, sử dụng ngôn ngữ lập trình Kotlin với Jetpack Compose cho giao diện người dùng hiện đại.
    - Sử dụng Firebase Cloud Firestore (NoSQL) để lưu trữ linh hoạt, truy xuất nhanh chóng và đồng bộ dữ liệu theo thời gian thực.
    - Tích hợp Firebase Authentication để hỗ trợ đăng ký, xác thực và phân quyền người dùng, đảm bảo tính bảo mật và riêng tư trong quá trình sử dụng.
    - Sử dụng Firebase Functions để xử lý logic backend và Firebase Storage để lưu trữ file.

- **Mục tiêu:**
    - TLUContact hướng đến trở thành cầu nối thông tin hiệu quả giữa các thành viên trong trường.
    - Giúp nâng cao hiệu suất làm việc và học tập.
    - Thúc đẩy quá trình số hóa trong giáo dục.

## Giao diện người dùng
https://www.figma.com/design/ezrsY5J8DKXXSKSgMs3o6s/Untitled?node-id=0-1&p=f&t=62QHq8iLZ3Ec66an-0

## Tính năng chính
- Thêm, sửa, xóa danh bạ với giao diện Jetpack Compose
- Tìm kiếm và lọc danh bạ theo tên, số điện thoại
- Nhập và xuất danh bạ từ các định dạng phổ biến
- Đồng bộ hóa danh bạ với Firebase Cloud Services
- Gửi tin nhắn và gọi điện trực tiếp từ ứng dụng
- Xác thực người dùng và phân quyền truy cập

## Công nghệ sử dụng
- **Ngôn ngữ lập trình:** Kotlin
- **Framework:** Android SDK (API 35)
- **UI Framework:** Jetpack Compose với Material Design 3
- **Cơ sở dữ liệu:** Firebase Cloud Firestore (NoSQL)
- **Xác thực:** Firebase Authentication
- **Storage:** Firebase Storage
- **Backend:** Firebase Functions (Node.js 22)
- **Công cụ phát triển:** Android Studio
- **Build System:** Gradle Kotlin DSL

## Yêu cầu hệ thống
- **Min SDK:** API 26 (Android 8.0)
- **Target SDK:** API 35 (Android 15)
- **Compile SDK:** API 35
- **Java Version:** 11

## Cấu trúc dự án
```plaintext
TLUContact/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/tlucontact/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── view/
│   │   │   │   │   └── HomeScreen.kt
│   │   │   │   └── (các file mã nguồn Kotlin khác)
│   │   │   ├── res/
│   │   │   │   └── (tài nguyên giao diện Compose)
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── (các file kiểm thử)
│   └── build.gradle.kts
├── functions/
│   ├── index.js
│   └── package.json (Node.js 22)
├── build.gradle.kts
└── settings.gradle.kts
```

## Cài đặt và chạy dự án

### Yêu cầu môi trường
- Android Studio Arctic Fox hoặc mới hơn
- JDK 11
- Firebase project được cấu hình

### Các bước cài đặt
1. Clone repository:
```bash
git clone https://github.com/OppenTona/TLUContact.git
```

2. Mở project trong Android Studio

3. Cấu hình Firebase:
   - Thêm file `google-services.json` vào thư mục `app/`
   - Đảm bảo Firebase Authentication, Firestore và Storage đã được kích hoạt

4. Sync project và build

5. Chạy ứng dụng trên device hoặc emulator

## Đóng góp
Mọi đóng góp để cải thiện dự án đều được chào đón. Vui lòng tạo issue hoặc pull request.

## Giấy phép
Dự án này được phát triển cho mục đích học tập tại Đại học Thủy Lợi.
