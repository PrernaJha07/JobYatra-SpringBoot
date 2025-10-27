# 🧭 JobYatra – Spring Boot Job Portal
### Empowering Careers Through Technology

## 🚀 Overview  
**JobYatra** is a complete **Spring Boot–based Job Portal Application** built to connect **job seekers and employers** on a single platform.  
It simplifies the recruitment process with an intuitive interface, secure login system, admin management, and email notifications.  

---

## 🎯 Key Features

### 👩‍💼 For Job Seekers
- Register and log in securely  
- Search and apply for jobs online  
- View application status and updates via email  

### 🏢 For Employers
- Register and log in securely  
- Post new job openings  
- Manage and review applications  
- Receive alerts for new candidates  

### 🧑‍💻 For Admin
- Oversee all users, jobs, and applications  
- Manage platform security and data integrity  
- Monitor overall system activity  

---

## 🧩 Technology Stack

| Layer | Technology Used |
|-------|------------------|
| **Backend** | Spring Boot (Java) |
| **Frontend** | HTML, CSS, JavaScript |
| **Database** | MySQL |
| **Build Tool** | Maven |
| **Security** | Spring Security |
| **Email Service** | JavaMailSender |
| **Version Control** | Git & GitHub |
| **IDE** | IntelliJ IDEA / Eclipse |

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the repository
```bash
git clone https://github.com/PrernaJha07/JobYatra-SpringBoot.git

```
2️⃣ Navigate to the project folder
cd JobYatra-SpringBoot

3️⃣ Configure Database

Create a MySQL database (for example, jobyatra_db)

Copy the file application.properties.example and rename it to application.properties

Open the new application.properties file and replace placeholders with your own credentials:

spring.datasource.username=your_username
spring.datasource.password=your_password
spring.mail.username=youremail@gmail.com
spring.mail.password=your_app_password


⚠️ Note:
Keep your real application.properties file local only (it’s ignored by Git).
Use the .example file as a public template for others.

4️⃣ Run the project
mvn spring-boot:run

5️⃣ Access the application

Open your browser and go to:
👉 http://localhost:8080

📬 Email Notifications

Automatically sends emails for:

New job postings

Application confirmations

Application status updates

🔐 Security Features

Password encryption using Spring Security

Role-based access: Admin, Employer, Job Seeker

CSRF protection and secure session management

📊 Future Enhancements

Cloud storage for resumes

Chat between employers and job seekers

AI-based job recommendations

Multi-language support

👩‍💻 Author

👤 Prerna Jha
📧 GitHub Profile - https://github.com/PrernaJha07

💬 Passionate about full-stack development and modern web technologies.

🏁 License

This project is licensed under the MIT License – feel free to use, modify, and enhance with proper credits.

🛡️ 2️⃣ .gitignore
# Maven target directory
/target/

# IDE directories
/.idea/
/.vscode/
/*.iml
/.classpath
/.project
/.settings/

# Logs and temp files
*.log
*.tmp
*.bak
*.swp

# OS generated files
.DS_Store
Thumbs.db

# Uploaded files
/uploads/

# Application config (keep local only)
src/main/resources/application.properties

# Backup or build files
*.class
*.jar
*.war
*.ear

⚙️ 3️⃣ application.properties.example
# ============================================
# Application Properties Example
# Rename this file to 'application.properties'
# and replace placeholders with real credentials.
# ============================================

# Server Configuration
server.port=8080

# MySQL Configuration (replace placeholders)
spring.datasource.url=jdbc:mysql://localhost:3306/job_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/job_portal

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.file-size-threshold=2KB
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
spring.servlet.multipart.location=./uploads

# File Storage
file.upload-dir=./uploads/

# Admin Configuration (placeholders)
admin.email=admin@example.com
admin.password=ADMIN_PASSWORD_PLACEHOLDER

# Certificate System
certificate.pass-threshold=80
certificate.test-retry-hours=24
certificate.questions-per-test=10

# Video settings
video.max-duration=10
video.max-size=52428800
video.allowed-types=video/mp4,video/webm,video/ogg
video.temp-dir=./temp_videos/

# Logging
logging.level.com.jobportal=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Email Configuration (placeholders)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@example.com
spring.mail.password=YOUR_EMAIL_PASSWORD_PLACEHOLDER
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
