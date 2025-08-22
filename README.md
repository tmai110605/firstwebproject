# EMAIL - Java Servlets/JSP (Eclipse + Tomcat 9)
 <img width="748" height="458" alt="image" src="https://github.com/user-attachments/assets/3f6834f2-8c7f-4d94-a339-6b216158da18" />
<img width="720" height="415" alt="image" src="https://github.com/user-attachments/assets/ee297b89-6e44-4a62-b069-b52c70ef3993" />
# EMAIL — Java Servlets/JSP (Eclipse + Tomcat)

Dự án mẫu theo kiến trúc MVC tối giản: **Servlet (Controller)** + **JSP/HTML (View)** + **Model/User + UserDB (mock)**.  
Phù hợp chạy **Eclipse** với **Tomcat 9** (do dùng `javax.servlet.*`).

---

## 1) Yêu cầu môi trường
- **JDK**: 11 hoặc 17 (khuyến nghị).
- **Apache Tomcat 9** (phù hợp `javax.servlet.*`).  
  > Nếu buộc dùng **Tomcat 10+** thì phải migrate code sang `jakarta.servlet.*` (xem mục “Ghi chú Tomcat 10”).
- (Tuỳ chọn) **Maven** nếu bạn build theo layout Maven.

---

## 2) Cấu trúc thư mục (layout Maven)
```
EMAIL/
├─ src/main/java/
│  └─ murach/
│     ├─ email/EmailListServlet.java
│     ├─ business/User.java
│     └─ data/UserDB.java
└─ src/main/webapp/
   ├─ index.html
   ├─ thanks.jsp
   ├─ styles/main.css
   └─ WEB-INF/web.xml
```

---

## 3) Chạy trực tiếp trong Eclipse (Run on Server)
1. **Add Tomcat 9** vào Eclipse: `Window → Preferences → Server → Runtime Environments → Add… → Tomcat v9.0`.
2. Project **Facets**: `Properties → Project Facets` → tick **Dynamic Web Module 3.1** và **Java 11/17**.
3. **Deployment Assembly** (quan trọng): phải có
   - `/src/main/webapp → /`
   - `/src/main/java  → /WEB-INF/classes`
4. `Project → Clean…` rồi **Run As → Run on Server** → chọn **Tomcat v9**.
5. Mở trình duyệt: `http://localhost:8080/EMAIL/`  
   - Điền form và submit → sẽ thấy `thanks.jsp` hiển thị thông tin.

> **Chia sẻ trong cùng mạng LAN**: người khác truy cập `http://<IP-máy-bạn>:8080/EMAIL/`  
> - Lấy IP (Windows): `ipconfig` → IPv4 Address.  
> - Mở firewall cổng **8080** nếu cần.  

---

## 4) Xuất WAR & deploy lên Tomcat cài sẵn
### A) Export WAR từ Eclipse
- `File → Export… → Web → WAR file` → chọn project `EMAIL` → xuất `EMAIL.war`.

### B) Deploy vào Tomcat
1. **Dừng Tomcat**.
2. Copy `EMAIL.war` vào `<TOMCAT_HOME>/webapps/`.
3. **Start Tomcat** → truy cập `http://<host>:8080/EMAIL/`.

> Muốn chạy ở **root**: đổi tên thành `ROOT.war` trước khi copy → URL: `http://<host>:8080/`.

---

## 5) (Tuỳ chọn) Build/Run bằng Maven & Docker
### Dockerfile (multi-stage) — dành cho layout Maven
```dockerfile
# Build WAR
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Run Tomcat 9 (javax.servlet)
FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
EXPOSE 8080
CMD ["catalina.sh", "run"]
```

**Chạy local** (nếu đã cài Docker Desktop):  
```bash
docker build -t email:v1 .
docker run --rm -p 8080:8080 email:v1
# Truy cập http://localhost:8080/
```

---

## 6) (Tuỳ chọn) Deploy lên Google Cloud Run (miễn phí mức cơ bản)
Không cần cài Docker local; Cloud Build sẽ đọc `Dockerfile` và build trên cloud.

```bash
gcloud auth login
gcloud config set project <PROJECT_ID>
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com

gcloud run deploy email \
  --source . \
  --region asia-southeast1 \
  --allow-unauthenticated \
  --memory 512Mi
# Kết quả: URL HTTPS public dạng https://email-xxxx.a.run.app
```

---

## 7) Lỗi thường gặp
- **404 khi POST /emailList**: thiếu hoặc sai `<servlet-mapping>` trong `WEB-INF/web.xml` → cần `url-pattern` là `/emailList`.  
- **500/EL không hiển thị**: đảm bảo `request.setAttribute("user", user)` trước khi forward tới `thanks.jsp`.  
- **405 (Method Not Allowed)**: form phải `method="post"`; servlet có `doPost`.  
- **ClassNotFound (Servlet)**: kiểm tra package + vị trí file trong `src/main/java`.
- **Không ai trong LAN vào được**: mở **firewall 8080** và dùng **IP** máy chạy Tomcat (không dùng `localhost`).

---

## 8) Ghi chú Tomcat 10 (Jakarta)
Nếu bạn deploy trên Tomcat 10+:
- Đổi import trong servlet từ `javax.servlet.*` → `jakarta.servlet.*`.
- Có thể cần cập nhật `web.xml` sang schema Jakarta EE 9/10 (web-app 5.0).
- Hoặc đơn giản nhất: dùng **Tomcat 9** để khỏi phải migrate.

---

## 9) Giấy phép & thông tin
Ví dụ học theo Murach — dùng cho mục đích học tập/demo.
