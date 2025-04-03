# TLUContact

Dự án bài tập lớn TLUContact của các sinh viên trường đại học Thủy lợi.

## Mục tiêu dự án
Mục tiêu của dự án TLUContact là phát triển một ứng dụng quản lý danh bạ giúp người dùng lưu trữ, quản lý và tìm kiếm thông tin liên lạc một cách hiệu quả và tiện lợi.

## Giao diện người dùng
https://www.figma.com/design/ezrsY5J8DKXXSKSgMs3o6s/Untitled?node-id=0-1&p=f&t=62QHq8iLZ3Ec66an-0

## Tính năng chính
- Thêm, sửa, xóa danh bạ (web), sửa thông tin cá nhân (di động)
- Tìm kiếm theo từ khóa (ví dụ: tên, chữ cái,...) và lọc danh bạ (ví dụ: đối với cán bộ thì lọc theo đơn vị, Sinh viên lọc theo khoa,...) - Di động
- Sắp xếp theo A-z/z-A
- Nhập và xuất danh bạ từ các định dạng phổ biến (Excel, CSV) - web
- Gửi tin nhắn và gọi điện thông qua ứng dụng (di động)
- Đăng nhập (gmail/outlook), đăng xuất, đăng kí (di động)

## Công nghệ sử dụng
- Ngôn ngữ lập trình: Kotlin
- Framework: Android SDK, Jetpack Compose – Hệ thống UI hiện đại cho Android
- Quản lý dữ liệu & API: SQLite, Firebase
- Môi trường phát triển (IDE): Android Studio
- Kiến trúc ứng dụng: MVVM (Model-View-ViewModel)
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