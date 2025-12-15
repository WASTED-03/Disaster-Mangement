# 🌍 SENTINEL: Real-Time Disaster Response System

> **"In the face of chaos, information is our strongest shield."**

**Sentinel** is a next-generation disaster management platform designed to bridge the gap between detection and action. By leveraging real-time data processing and instant communication channels, Sentinel empowers authorities to respond to emergencies with unprecedented speed and precision.

![System Status](https://img.shields.io/badge/System-ONLINE-success?style=for-the-badge)
![Alert Level](https://img.shields.io/badge/Alert%20Level-CRITICAL-red?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

---

## ⚡ The Arsenal (Tech Stack)

Built on a fortress of modern technologies, ensuring reliability when it matters most.

### 🛡️ Backend (The Core)
*   **Java 21**: The bedrock of our performance.
*   **Spring Boot 3.5**: Rapid, secure, and scalable microservices architecture.
*   **Spring Security**: Ironclad authentication (JWT) and authorization.
*   **WebSocket (Stomp/SockJS)**: The nervous system, delivering sub-second alert propagation.
*   **MySQL**: Robust data persistence for critical incident records.

### ⚔️ Frontend (The Interface)
*   **React 19**: Cutting-edge UI library for a reactive user experience.
*   **Vite**: Blazing fast build tool and dev server.
*   **Tailwind CSS**: Utility-first styling for a sleek, responsive, and mobile-ready command center.
*   **Axios**: Seamless API integration.

---

## 🚀 Key Capabilities

### 📡 Live Intel (Real-Time Alerts)
Don't wait for updates—watch them happen. Sentinel's WebSocket integration pushes critical alerts to the dashboard instantly. No refreshing required.

### 🔐 Secure Command Center
Role-Based Access Control (RBAC) ensures that only authorized personnel can trigger alerts or view sensitive data. Secured via JWT protocols.

### 📊 Tactical Dashboard
A unified view of the battlefield.Visualize active threats, monitor system status, and coordinate response efforts from a single pane of glass.

---

## 🛠️ Mobilization Protocols (Setup Guide)

Ready to deploy Sentinel? Follow these protocols.

### Prerequisites
*   Java JDK 21+
*   Node.js 18+
*   MySQL Server

### 1. Database Initialization
Create a MySQL database named `disaster_management_db` (or update `application.properties` to match your config).

```sql
CREATE DATABASE disaster_management_db;
```

### 2. Backend Deployment
Navigate to the root directory and launch the Spring Boot core.

```bash
# Windows
./mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```
*The Core will initialize on authenticated port `8080`.*

### 3. Frontend Deployment
Open a new terminal and infiltrate the `frontend` sector.

```bash
cd frontend
npm install
npm run dev
```
*The Interface will be accessible at `http://localhost:5173`.*

---

## 🤝 Contributing
The world needs heroes. Fork the repository, create your feature branch, and submit a Pull Request.

---

*"Safety is not an accident. It is a choice."*
