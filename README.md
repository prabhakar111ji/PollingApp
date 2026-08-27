# 🗳️ Polling App — Full-Stack Polling Application

A full-stack polling application built with **React + Vite** frontend and **Spring Boot** backend, featuring JWT authentication, real-time voting, comments, likes, and responsive Material UI design.

---

## ✨ Features

- **User Authentication** — Register, login with JWT-based auth
- **Create Polls** — Post polls with multiple options and expiration dates
- **Vote** — Cast votes on active polls (one vote per user per poll)
- **Results** — Dynamic percentage calculation with animated progress bars
- **Comments** — Comment on polls with real-time updates
- **Likes** — Like/unlike polls (toggle behavior)
- **Views** — Track how many times a poll has been viewed
- **My Polls** — View and manage your created polls
- **Poll Expiration** — Automatic expiration with visual indicators
- **Ownership Controls** — Delete only your own polls
- **Responsive UI** — Works on desktop, tablet, and mobile

---

## 🛠️ Tech Stack

| Layer      | Technology                                      |
|------------|------------------------------------------------|
| Frontend   | React 18, Vite 5, Material UI 5, Axios, Notistack |
| Backend    | Java 21, Spring Boot 3.2, Spring Security, JPA |
| Auth       | JWT (jjwt 0.12), BCrypt                        |
| Database   | PostgreSQL (Production) / H2 (Local)           |
| Deployment | Render (Monolithic Full-Stack Web Service)     |

---

## 🏗️ Architecture

```
Client Browser
        │
        │  HTTPS
        ▼
Spring Boot Backend (Monolithic on Render)
  ├── Serves React Static Files (from /src/main/resources/static)
  └── Exposes REST API (on /api/**)
        │
        │  JPA / Hibernate
        ▼
PostgreSQL Database (Neon)
```

---

## 📁 Folder Structure

```
polling-app/
├── backend/
│   ├── pom.xml
│   ├── .env.example
│   └── src/main/java/com/pollingapp/
│       ├── config/          # Security, CORS config
│       ├── controller/      # REST controllers & Frontend forwarder
│       ├── dto/             # Request/Response DTOs
│       ├── entity/          # JPA entities
│       ├── exception/       # Custom exceptions + global handler
│       ├── repository/      # Spring Data repositories
│       ├── security/        # JWT service, filter, UserDetailsService
│       └── service/         # Business logic
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── components/      # Navbar, PollCard, ProtectedRoute
│       ├── pages/           # Register, Login, Dashboard, etc.
│       ├── services/        # Axios API calls
│       └── utils/           # Auth helpers
├── Dockerfile               # Monolithic full-stack Docker build
├── README.md
└── .gitignore
```

---

## 📋 Prerequisites

- **Java 17/21** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) (or use `mvnw`)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **Git** — [Download](https://git-scm.com/)

---

## 🚀 Local Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd polling-app
```

### 2. Backend Setup

```bash
cd backend
```

Create a `.env` file in the `backend/` folder if you want to test with your production database, otherwise the app will default to a local in-memory H2 database automatically:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<your-neon-host>:5432/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=<your-db-username>
SPRING_DATASOURCE_PASSWORD=<your-db-password>
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
JWT_SECRET=superSecretKeyForPollingAppThatIsLongEnough123!@#
```

Run the backend:
```bash
mvn spring-boot:run
```

Verify: Open `http://localhost:8080/api/health`

### 3. Frontend Setup

```bash
cd frontend
npm install
```

Run the frontend in development mode:
```bash
npm run dev
```

Open: **http://localhost:5173**

---


## 🌐 Deployment (Render)

This project is configured for **Monolithic Full-Stack Deployment** via a single `Dockerfile` at the root of the repository. When built, the Dockerfile compiles the React frontend and embeds it directly into the Spring Boot backend, meaning you only need to host **one** service on Render!

### Step 1: Push to GitHub

Ensure all your code is pushed to your GitHub repository.

### Step 2: Database Setup (Neon)

1. Go to [neon.tech](https://neon.tech/) and create a free PostgreSQL project.
2. Go to the dashboard and copy your connection string (it looks like `postgres://username:password@host/dbname`).

### Step 3: Deploy on Render

1. Go to [render.com](https://render.com/) and create a new **Web Service**.
2. Select your GitHub repository.
3. Configure the following settings:
   - **Name**: `polling-app` (or whatever you prefer)
   - **Language**: `Docker`
   - **Branch**: `main`
   - **Root Directory**: *(Leave this completely empty)*
   - **Dockerfile Path**: `./Dockerfile`
   - **Instance Type**: `Free`
4. Scroll down to **Environment Variables** and add the following:

   | Key | Value |
   |-----|-------|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<your-neon-host>:5432/<dbname>?sslmode=require` |
   | `SPRING_DATASOURCE_USERNAME` | `<your-neon-username>` |
   | `SPRING_DATASOURCE_PASSWORD` | `<your-neon-password>` |
   | `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | `org.postgresql.Driver` |
   | `JWT_SECRET` | *(Enter a long random string here)* |

5. Click **Deploy Web Service**!

Render will now build your frontend and backend together and deploy them to a single URL. Both your React UI and Spring Boot API will be hosted on the same link seamlessly.


