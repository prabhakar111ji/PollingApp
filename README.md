# 🗳️ Polling App — Full-Stack Polling & Voting Platform

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-556B2F?style=for-the-badge&logo=render&logoColor=white)](https://pollingapp-c178.onrender.com/)

[![React](https://img.shields.io/badge/Frontend-React%2018%20%7C%20Vite%205-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Auth-JWT%20%2B%20Spring%20Security-red?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20(Neon)-336791?logo=postgresql&logoColor=white)](https://neon.tech/)
[![Deployment](https://img.shields.io/badge/Deploy-Docker%20%7C%20Render-black?logo=render&logoColor=white)](https://render.com/)

> 🚀 **Live Demo:** [https://pollingapp-c178.onrender.com/](https://pollingapp-c178.onrender.com/)

A modern full-stack polling web application featuring JWT authentication, real-time voting, dynamic result visualization, comments, likes, view counting, and a fully responsive Material UI design.

---

## ✨ Features

- **🔐 User Authentication** — Secure registration and login with JWT (JSON Web Tokens) & BCrypt password hashing.
- **📊 Interactive Polls** — Create polls with custom options and expiration dates.
- **🗳️ Real-Time Voting** — Enforces one vote per user per poll with instant tallying.
- **📈 Dynamic Results** — Visual progress bars with automated percentage calculations.
- **💬 Comments System** — Engage in discussions under polls with instant updates.
- **❤️ Likes & Views Tracking** — Like/unlike toggle mechanism and poll view analytics.
- **👤 User Dashboard ("My Polls")** — View, manage, and delete polls created by the logged-in user.
- **⏳ Expiration Handling** — Auto-closes polls upon reaching expiration time with distinct UI badges.
- **📱 Responsive UI** — Built with Material UI 5 with support for mobile, tablet, and desktop viewports.

---

## 🛠️ Tech Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Frontend** | React 18, Vite 5, JavaScript (ES6+) | Single Page Application (SPA) |
| **UI Framework** | Material UI (MUI v5), Emotion, Notistack | Modern responsive design & notification toasts |
| **HTTP Client** | Axios | Configured with automatic JWT request/response interceptors |
| **Backend** | Java 17 / 21, Spring Boot 3.2.5 | RESTful API built with Spring Data JPA & Hibernate |
| **Security** | Spring Security 6, JJWT (0.12.5) | Stateless authentication & BCrypt password hashing |
| **Database** | PostgreSQL (Neon Cloud) / H2 (Local) | Production cloud DB & fast zero-setup local DB |
| **Containerization**| Multi-stage Docker (`Dockerfile`) | Monolithic container embedding React build into Spring Boot |
| **Deployment** | Render | Hosted full-stack web service |

---

## 🏗️ Architecture

```
  Client Browser (HTTPS)
          │
          ▼
  Spring Boot Service (Render / Docker on Port 8080)
  ├── 🌐 Serves React Static UI (/src/main/resources/static)
  └── 🔌 Exposes REST API (/api/**)
          │
          │ JPA / Hibernate (PostgreSQL Connection)
          ▼
  Neon Cloud PostgreSQL Database
```

---

## 📁 Project Structure

```
PollingApp/
├── backend/                             # Spring Boot Backend
│   ├── pom.xml                          # Maven dependencies & plugins
│   ├── .env.example                     # Backend environment template
│   └── src/
│       ├── main/
│       │   ├── java/com/pollingapp/
│       │   │   ├── config/              # Security, CORS & Web forwarder config
│       │   │   ├── controller/          # REST API endpoints (Auth, Poll, Comment, Vote)
│       │   │   ├── dto/                 # Request & Response DTOs
│       │   │   ├── entity/              # JPA Entities (User, Poll, Option, Vote, Comment)
│       │   │   ├── exception/           # Custom exceptions & global exception handler
│       │   │   ├── repository/          # Spring Data JPA repositories
│       │   │   ├── security/            # JWT Token provider, filter & UserDetailsService
│       │   │   └── service/             # Business logic implementations
│       │   └── resources/
│       │       ├── application.properties        # Production PostgreSQL configuration
│       │       └── application-local.properties  # Local in-memory H2 profile
│       └── test/                        # Integration and unit tests
├── frontend/                            # React + Vite Frontend
│   ├── package.json                     # Frontend dependencies & scripts
│   ├── vite.config.js                   # Vite configuration
│   ├── .env.example                     # Frontend environment template
│   ├── index.html                       # HTML entry point
│   └── src/
│       ├── components/                  # UI components (Navbar, PollCard, ProtectedRoute)
│       ├── pages/                       # Pages (Dashboard, CreatePoll, Login, Register, MyPolls)
│       ├── services/                    # API services (authService, pollService)
│       └── utils/                       # JWT token & auth helpers
├── Dockerfile                           # Multi-stage Docker build (React + Spring Boot combined)
├── API.md                               # Complete REST API documentation
├── LICENSE                              # MIT License
├── README.md                            # Project documentation
└── .gitignore                           # Git ignore rules
```

---

## ⚙️ Environment Variables

### Backend (Render Environment Variables / `backend/.env`)
| Variable | Description | Example |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://<host>:5432/<dbname>?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `<neon-username>` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `<neon-password>` |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | PostgreSQL driver | `org.postgresql.Driver` |
| `JWT_SECRET` | Secret key for JWT token signing | `yourSecretKeyForPollingAppThatIsLongEnough123` |
| `JWT_EXPIRATION` | Token expiration time (ms) | `86400000` (24 hours) |

---

## 🚀 Local Development Setup

### Prerequisites
- **Java JDK 17 or 21**: [Download Adoptium](https://adoptium.net/)
- **Maven 3.8+**: [Download Maven](https://maven.apache.org/)
- **Node.js 18+ & npm**: [Download Node.js](https://nodejs.org/)
- **Git**: [Download Git](https://git-scm.com/)

---

### 1. Clone the Repository

```bash
git clone https://github.com/prabhakar111ji/PollingApp.git
cd PollingApp
```

---

### 2. Run Backend (with Local H2 In-Memory DB)

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
- **Backend API**: `http://localhost:8080/api`
- **H2 Database Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:poll_db`, User: `sa`, Password: *empty*)

---

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

- **Frontend App**: `http://localhost:5173`

---

## 🌐 Production Deployment (Render + Neon PostgreSQL)

The project uses a monolithic multi-stage **`Dockerfile`** that builds both the React frontend and Spring Boot backend into a single container:

1. **Database (Neon)**:
   - Create a free PostgreSQL database on [Neon.tech](https://neon.tech/).
   - Copy the PostgreSQL connection credentials.

2. **Web Service (Render)**:
   - Create a new **Web Service** on [Render.com](https://render.com/) and connect this repository.
   - Choose runtime **Docker** and branch **main**.
   - Under **Environment Variables**, set:
     - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<neon-host>:5432/<dbname>?sslmode=require`
     - `SPRING_DATASOURCE_USERNAME` = `<your-neon-username>`
     - `SPRING_DATASOURCE_PASSWORD` = `<your-neon-password>`
     - `SPRING_DATASOURCE_DRIVER_CLASS_NAME` = `org.postgresql.Driver`
     - `JWT_SECRET` = `yourRandomSecretKey`
   - Click **Deploy Web Service**.

---

## 📖 API Documentation

Complete REST API specifications and request/response payloads:
👉 **[View Complete API Documentation (API.md)](API.md)**

### Key Endpoints:
- `POST /api/auth/signup` — Register a new account
- `POST /api/auth/login` — Login and receive JWT token
- `GET /api/user/poll` — Fetch all polls
- `POST /api/user/poll` — Create a new poll (Authenticated)
- `GET /api/user/poll/{id}` — Get single poll details with comments
- `GET /api/user/poll/my` — Get user's created polls (Authenticated)
- `DELETE /api/user/poll/{id}` — Delete a poll by ID (Owner only)
- `POST /api/user/poll/vote` — Cast a vote (Authenticated)
- `POST /api/user/poll/{id}/like` — Toggle like/unlike (Authenticated)
- `POST /api/user/poll/comment` — Add comment (Authenticated)
- `GET /api/health` — Backend health check

---

## 🛡️ License

This project is open-source and available under the [MIT License](LICENSE).
