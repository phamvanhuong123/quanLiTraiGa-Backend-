# GIAI ĐOẠN 1: BUILD ỨNG DỤNG
# Sử dụng một image có chứa sẵn Maven và JDK để build mã nguồn
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Đặt thư mục làm việc bên trong container
WORKDIR /workspace

# Sao chép tệp pom.xml để tải các dependency trước
COPY pom.xml .
# Sao chép toàn bộ mã nguồn
COPY src src

# Chạy lệnh build của Maven để tạo ra tệp .jar. Bỏ qua tests để build nhanh hơn.
RUN mvn package -DskipTests

# GIAI ĐOẠN 2: TẠO IMAGE CHẠY ỨNG DỤNG
# Sử dụng một image JRE (Java Runtime Environment) gọn nhẹ vì chỉ cần chạy ứng dụng
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
# Sao chép tệp .jar đã được build từ giai đoạn 'build' vào image hiện tại
COPY --from=build /workspace/target/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]