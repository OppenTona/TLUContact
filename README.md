# TLUContact

Dự án bài tập lớn TLUContact của các sinh viên trường đại học Thủy lợi.

## Mục tiêu dự án
Mục tiêu của dự án TLUContact là phát triển một ứng dụng quản lý danh bạ giúp người dùng lưu trữ, quản lý và tìm kiếm thông tin liên lạc một cách hiệu quả và tiện lợi.

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