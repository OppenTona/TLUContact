# TLUContact

Dự án bài tập lớn TLUContact của các sinh viên trường đại học Thủy lợi.

## Giới thiệu về đề tài
Trong thời đại chuyển đổi số mạnh mẽ, việc ứng dụng công nghệ thông tin vào quản lý và vận hành hệ thống giáo dục không chỉ là xu hướng mà còn là nhu cầu tất yếu. Tuy nhiên, tại Trường Đại học Thủy Lợi, việc quản lý và tra cứu thông tin liên lạc của cán bộ, giảng viên và sinh viên vẫn chủ yếu dựa vào các tài liệu giấy hoặc file Word, dẫn đến nhiều bất cập như khó khăn trong tìm kiếm, cập nhật chậm trễ và nguy cơ mất mát dữ liệu. Đặc biệt, chưa có một hệ thống danh bạ tập trung nào giúp sinh viên và giảng viên có thể liên lạc trực tiếp với nhau, khiến việc trao đổi thông tin thường phải thông qua giáo viên phụ trách liên chi đoàn, gây mất thời gian và giảm hiệu quả giao tiếp.
Xuất phát từ thực tế đó, đề tài “TLUContact – Ứng dụng danh bạ điện tử cho Đại học Thủy Lợi” ra đời với mục tiêu xây dựng một nền tảng hiện đại, an toàn và tiện lợi để quản lý thông tin liên lạc trong nhà trường. Ứng dụng được phát triển trên hệ điều hành Android, sử dụng ngôn ngữ lập trình Kotlin kết hợp với cơ sở dữ liệu NoSQL của Firebase (Cloud Firestore) nhằm đảm bảo khả năng lưu trữ linh hoạt, truy xuất nhanh chóng và đồng bộ dữ liệu theo thời gian thực. Ngoài ra, hệ thống còn tích hợp Firebase Authentication để hỗ trợ đăng ký, xác thực và phân quyền người dùng, giúp đảm bảo tính bảo mật và riêng tư trong quá trình sử dụng.
Không chỉ đơn thuần là một danh bạ điện tử, TLUContact hướng đến trở thành cầu nối thông tin hiệu quả giữa các thành viên trong trường, giúp sinh viên dễ dàng tìm kiếm, liên hệ với giảng viên và ngược lại mà không cần qua trung gian. Nhờ đó, ứng dụng không chỉ góp phần nâng cao hiệu suất làm việc và học tập mà còn thúc đẩy quá trình số hóa trong giáo dục, giúp Đại học Thủy Lợi tiệm cận với xu hướng quản lý hiện đại và thông minh.

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