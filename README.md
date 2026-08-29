# 🗳️ Polling App — Full-Stack Polling & Voting Platform

[![React](https://img.shields.io/badge/Frontend-React%2018%20%7C%20Vite%205-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203.2-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Auth-JWT%20%2B%20Spring%20Security-red?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20%7C%20MySQL%20%7C%20H2-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Deployment](https://img.shields.io/badge/Deploy-Docker%20%7C%20Render%20%7C%20Vercel-black?logo=render&logoColor=white)](https://render.com/)

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
| **Frontend** | React 18, Vite 5, JavaScript (ES6+) | Fast SPA with Vite dev server |
| **UI Library** | Material UI (MUI v5), Emotion, Notistack | Modern theme, snackbar alerts, custom icons |
| **HTTP Client** | Axios | Configured with automatic JWT interceptors |
| **Backend** | Java 17 / 21, Spring Boot 3.2.5 | RESTful API with Spring Data JPA & Hibernate |
| **Security** | Spring Security 6, JJWT (0.12.5) | Stateless session, BCrypt encryption |
| **Databases** | PostgreSQL, MySQL, H2 (In-Memory) | Multi-database support via Spring JPA |
| **Containerization**| Multi-stage Docker (`Dockerfile`) | Single monolithic image combining UI + API |
| **Deployment** | Render, Vercel, Neon DB | Production cloud hosting |

---

## 🏗️ Architecture & Deployment Overview

The application supports both **Monolithic (Single Service)** and **Decoupled (Multi-Service)** deployments:

### Monolithic Full-Stack Architecture (Render / Docker)
```
  Client Browser (HTTPS)
          │
          ▼
  Spring Boot Service (Render / Docker container on Port 8080)
  ├── 🌐 Serves React Static UI (/src/main/resources/static)
  └── 🔌 Exposes REST API (/api/**)
          │
          │ JPA / Hibernate Connection
          ▼
  Managed Database (Neon PostgreSQL / MySQL)
```

---

## 📁 Project Structure

```
PollingApp/
├── backend/                             # Spring Boot 3.2 Backend
│   ├── pom.xml                          # Maven dependencies & plugins
│   ├── Dockerfile                       # Standalone backend container config
│   ├── .env.example                     # Backend environment template
│   └── src/
│       ├── main/
│       │   ├── java/com/pollingapp/
│       │   │   ├── config/              # Security, CORS & Web forwarder config
│       │   │   ├── controller/          # REST API endpoints (Auth, Poll, Comment, Vote)
│       │   │   ├── dto/                 # Request & Response payload objects
│       │   │   ├── entity/              # JPA Data Entities (User, Poll, Option, Vote, Comment)
│       │   │   ├── exception/           # Custom exceptions & global exception handler
│       │   │   ├── repository/          # Spring Data JPA repositories
│       │   │   ├── security/            # JWT Token provider, filter & UserDetailsService
│       │   │   └── service/             # Business logic implementations
│       │   └── resources/
│       │       ├── application.properties        # Main configuration (PostgreSQL / MySQL)
│       │       └── application-local.properties  # Local H2 in-memory profile
│       └── test/                        # Unit and integration tests
├── frontend/                            # React 18 + Vite 5 Frontend
│   ├── package.json                     # Frontend dependencies & scripts
│   ├── vite.config.js                   # Vite configuration
│   ├── vercel.json                      # Vercel SPA routing configuration
│   ├── .env.example                     # Frontend environment template
│   ├── index.html                       # HTML entry point
│   └── src/
│       ├── components/                  # Reusable UI (Navbar, PollCard, ProtectedRoute)
│       ├── pages/                       # App views (Dashboard, CreatePoll, Login, Register, MyPolls)
│       ├── services/                    # Axios API service layers (authService, pollService)
│       └── utils/                       # JWT token & auth helpers
├── Dockerfile                           # Multi-stage Docker build (React + Spring Boot combined)
├── API.md                               # Complete REST API documentation
├── README.md                            # Project documentation
└── .gitignore                           # Git ignore rules
```

---

## ⚙️ Environment Variables

### Backend (`backend/.env` or Render Dashboard)
| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `PORT` | Server listening port | `8080` |
| `SPRING_DATASOURCE_URL` | JDBC Database Connection URL | `jdbc:postgresql://<host>:5432/<db>?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `<neon-user>` or `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `<neon-password>` |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | JDBC driver class | `org.postgresql.Driver` or `com.mysql.cj.jdbc.Driver` |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 256 bits) | `superSecretKeyForPollingAppThatIsLongEnough123!@#` |
| `JWT_EXPIRATION` | Token validity duration in milliseconds | `86400000` (24 Hours) |
| `FRONTEND_URL` | Allowed CORS origin (for decoupled setup) | `http://localhost:5173` |

### Frontend (`frontend/.env` or Vercel Dashboard)
| Variable | Description | Example |
| :--- | :--- | :--- |
| `VITE_API_URL` | Backend REST API Base URL | `http://localhost:8080/api` or `/api` (for monolithic) |

---

## 🚀 Local Development Setup

### Prerequisites
- **Java JDK 17 or 21**: [Download Adoptium](https://adoptium.net/)
- **Maven 3.8+**: [Download Maven](https://maven.apache.org/) (or use system maven)
- **Node.js 18+ & npm**: [Download Node.js](https://nodejs.org/)
- **Git**: [Download Git](https://git-scm.com/)

---

### 1. Clone the Repository

```bash
git clone https://github.com/prabhakar111ji/PollingApp.git
cd PollingApp
```

---

### 2. Backend Setup

You can run the backend with an in-memory **H2 Database** (zero setup required) or connect to your local MySQL / PostgreSQL.

#### Run with H2 In-Memory Database (Fastest):
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
- API Base: `http://localhost:8080/api`
- H2 Web Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:poll_db`, User: `sa`, Password: *empty*)

#### Run with MySQL / PostgreSQL:
1. Create a `.env` file inside the `backend/` directory:
   ```env
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=poll_db
   DB_USERNAME=root
   DB_PASSWORD=your_password
   JWT_SECRET=superSecretKeyForPollingAppThatIsLongEnough123!@#
   ```
2. Start the server:
   ```bash
   mvn spring-boot:run
   ```

---

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Open your browser at **http://localhost:5173** to interact with the application.

---

## 🌐 Production Deployment Guide

### Option 1: Full-Stack Monolithic Deployment on Render (Recommended)

This method packages the React frontend inside the Spring Boot JAR via the root `Dockerfile`, serving both from **one single free Render web service**.

#### Step 1: Create a Managed PostgreSQL Database (Neon)
1. Go to [Neon.tech](https://neon.tech/) and create a free project.
2. Note your database credentials (`host`, `database`, `user`, `password`).

#### Step 2: Deploy Service on Render
1. Go to [Render.com](https://render.com/) and click **New +** > **Web Service**.
2. Connect your GitHub repository: `https://github.com/prabhakar111ji/PollingApp`.
3. Configure the following settings:
   - **Name**: `polling-app`
   - **Runtime**: `Docker`
   - **Branch**: `main`
   - **Root Directory**: *(Leave empty)*
   - **Dockerfile Path**: `./Dockerfile`
   - **Instance Type**: `Free`
4. Under **Environment Variables**, add:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<neon-host>:5432/<dbname>?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME`: `<your-neon-username>`
   - `SPRING_DATASOURCE_PASSWORD`: `<your-neon-password>`
   - `SPRING_DATASOURCE_DRIVER_CLASS_NAME`: `org.postgresql.Driver`
   - `JWT_SECRET`: `yourGeneratedSuperSecretKeyWith64CharactersMin`
5. Click **Deploy Web Service**.

---

### Option 2: Decoupled Deployment (Frontend on Vercel + Backend on Render)

If you prefer deploying frontend and backend independently:

1. **Backend**:
   - Deploy `backend/` on Render using `backend/Dockerfile` or Maven build.
   - Set environment variables as listed above, including `FRONTEND_URL=https://your-frontend.vercel.app`.
2. **Frontend**:
   - Import the `frontend/` directory into [Vercel](https://vercel.com).
   - Set the environment variable: `VITE_API_URL=https://your-backend.onrender.com/api`.
   - Vercel will build using the configuration in [vercel.json](file:///f:/Placement/Projects/PollingApp/frontend/vercel.json).

---

## 📖 API Documentation

The full REST API specification, including request/response examples and authentication headers, is documented in:
👉 **[View Complete API Documentation (API.md)](API.md)**

### Key Endpoint Summary:
- `POST /api/auth/signup` — Register a new account
- `POST /api/auth/login` — Login and receive JWT token
- `GET /api/polls` — Fetch paginated/filtered list of active polls
- `POST /api/polls` — Create a new poll (Authenticated)
- `POST /api/polls/{id}/vote` — Cast a vote on a poll option (Authenticated)
- `POST /api/polls/{id}/comments` — Add a comment to a poll (Authenticated)
- `POST /api/polls/{id}/like` — Toggle like on a poll (Authenticated)
- `GET /api/polls/my-polls` — Retrieve polls created by current user (Authenticated)

---

## 🛡️ License

This project is open-source and available under the [MIT License](LICENSE).
