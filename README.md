<h1 align="center">🎓 DIU CampusCart</h1>
<p align="center">
  A smart buy & sell platform exclusively for DIU students, built with ❤️ using Spring Boot, Tailwind CSS, and Thymeleaf.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Active-success?style=flat-square"/>
  <img src="https://img.shields.io/badge/Backend-Java%20%7C%20SpringBoot-blue?style=flat-square"/>
  <img src="https://img.shields.io/badge/Frontend-TailwindCSS%20%7C%20HTML-informational?style=flat-square"/>
  <img src="https://img.shields.io/badge/Database-H2-lightgrey?style=flat-square"/>
</p>


## 📌 Project Overview

**DIU CampusCart** is a web-based classified ads platform made specifically for the students of **Daffodil International University**. The platform enables DIU students to list items for sale, browse available products, and submit buy requests — all in a secure and student-only ecosystem.

> 🔐 Only verified DIU email users (@diu.edu.bd) can register and use the platform.


## ✨ Key Features

- 🧾 **DIU Email Verification** via JavaMailSender
- 👤 **Session-Based Authentication** for Login/Logout
- 📋 **Product Listing with Category Selection**
- 💬 **Buy Request via Email with Accept/Reject Option**
- ✅ **Seller Accept Link (Email Confirmation)**
- 📦 **Mark Product as “Sold Out”**
- 📊 **Seller Dashboard for Product Management**
- 🎨 **Beautiful UI with Tailwind CSS + DaisyUI**
- 💾 **H2 Persistent Database**

## 🧰 Tech Stack

| Layer       | Technology                      |
|-------------|----------------------------------|
| Backend     | Java, Spring Boot                |
| Frontend    | HTML, Tailwind CSS, DaisyUI      |
| Templating  | Thymeleaf                        |
| Database    | H2 (File-based Persistent Mode)  |
| Email       | JavaMailSender (Gmail SMTP)      |
| Auth        | Session-based Authentication     |


## 📂 Project Structure

📦 DIU-CampusCart
├── src
│   ├── main
│   │   ├── java/dev/noman/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   └── repository/
│   │   └── resources/
│   │       ├── templates/         # HTML templates
│   │       └── application.properties
├── data/
│   └── noman.mv.db                # H2 database

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven
- Internet (for sending email)

### 🔧 Setup Steps

1. **Clone the repository:**
   git clone https://github.com/noman797/DIU-CampusCart.git
   cd DIU-CampusCart

2. **Configure email in `application.properties`:**
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.protocol=smtp
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true

3. **Run the application:**
   ./mvnw spring-boot:run

4. **Visit in Browser:**
   http://localhost:8080


## 🧠 Upcoming Features

- 📱 Mobile App Integration (Flutter or React Native)
- 🧠 AI-based Smart Product Suggestions
- 🔐 Blockchain for Listing Verification
- 📍 Location-Based Product Discovery
- 🏆 Gamification & Points System
- 📬 In-App Messaging Between Users

## 💬 Feedback & Support

- Found a bug? Open an [Issue](https://github.com/noman797/DIU-CampusCart/issues)
- Want to contribute? Fork the repo & create a pull request.
- Have a suggestion? Let’s connect!

## 🌟 Show Your Support

If you like this project, don’t forget to ⭐ the repository!

<p align="center">
  Made with ❤️ by Team CampusCart
</p>

