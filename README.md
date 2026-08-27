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

## 🧪 Testing

### Manual Test Checklist

1. ✅ Open http://localhost:5173
2. ✅ Register user: `test@test.com` / `password123`
3. ✅ See success notification → redirected to login
4. ✅ Login with same credentials
5. ✅ See dashboard (empty initially)
6. ✅ Navigate to "Create Poll"
7. ✅ Create a poll with 3 options, future expiration
8. ✅ See success → redirected to dashboard
9. ✅ See your poll on dashboard
10. ✅ Vote on the poll → See results with percentages
11. ✅ Try voting again → See error (duplicate)
12. ✅ Like the poll → Like count updates
13. ✅ Click "View Details" → See poll details page (view count should increment)
14. ✅ Add a comment → Comment appears
15. ✅ Like again → Unlike (toggle)
16. ✅ Navigate to "My Polls"
17. ✅ Delete the poll → Confirmation dialog → Poll removed

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

---

## 🎓 Interview Explanation

### Architecture
> "This is a full-stack polling application with a React frontend embedded into a Spring Boot backend for a monolithic deployment. The backend serves the static React files on standard routes, while exposing the REST API on the `/api` path. It uses Spring Data JPA to communicate with a managed PostgreSQL database on Neon. Authentication is stateless using JWT tokens."

### JWT Authentication Flow
> "On login, the backend validates credentials against BCrypt-hashed passwords, generates a JWT with the user's email as subject and configurable expiration, and returns it. The frontend stores the JWT and attaches it as a Bearer token on every API request via an Axios interceptor. The backend has a JWT authentication filter that intercepts every request, extracts and validates the token, and sets the SecurityContext."

### Voting Flow
> "When a user votes: (1) authenticate via JWT, (2) find the poll and verify it exists, (3) find the option and verify it belongs to that poll, (4) check the poll hasn't expired, (5) check the user hasn't already voted via a unique constraint on user_id+poll_id, (6) create the vote record, (7) increment option vote count and poll total vote count, (8) all within a @Transactional method for atomicity."

### Database Relationships
> "Users have one-to-many relationships with polls, votes, likes, and comments. Polls have one-to-many with options, votes, likes, and comments. Votes have a unique constraint on (user_id, poll_id) to prevent duplicate voting at the database level. Likes have the same constraint for duplicate prevention."

### How Duplicate Voting is Prevented
> "Three layers: (1) Frontend disables the vote button after voting, (2) Backend service checks `voteRepository.existsByUserIdAndPollId()` before creating a vote, (3) Database has a unique constraint on (user_id, poll_id) as the ultimate safety net."

### How Expiration Works
> "Each poll has an `expiredAt` timestamp. The `Poll.isExpired()` method compares it against the current time. The backend rejects votes on expired polls with a 400 error. The frontend shows results instead of voting controls for expired polls."

### Monolithic Deployment Architecture
> "To simplify deployment and avoid CORS issues, I configured a multi-stage Dockerfile. Stage 1 uses Node.js to build the Vite React app into static files. Stage 2 uses Maven to copy those static files into Spring Boot's `/static` directory and packages the JAR. Stage 3 runs the JAR. This allows Render to host the entire full-stack application inside a single Free Tier container."
