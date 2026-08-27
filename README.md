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
- **My Polls** — View and manage your created polls
- **Poll Expiration** — Automatic expiration with visual indicators
- **Ownership Controls** — Delete only your own polls
- **Responsive UI** — Works on desktop, tablet, and mobile

---

## 🛠️ Tech Stack

| Layer      | Technology                                      |
|------------|------------------------------------------------|
| Frontend   | React 18, Vite 5, Material UI 5, Axios, Notistack |
| Backend    | Java 17, Spring Boot 3.2, Spring Security, JPA |
| Auth       | JWT (jjwt 0.12), BCrypt                        |
| Database   | MySQL 8                                         |
| Deployment | Vercel (frontend), Railway (backend + MySQL)    |

---

## 🏗️ Architecture

```
React Frontend (Vite)
        │
        │  REST API + JWT Bearer Token
        ▼
Spring Boot Backend
        │
        │  JPA / Hibernate
        ▼
MySQL Database
```

---

## 📁 Folder Structure

```
polling-app/
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── .env.example
│   └── src/main/java/com/pollingapp/
│       ├── config/          # Security, CORS config
│       ├── controller/      # REST controllers
│       ├── dto/             # Request/Response DTOs
│       ├── entity/          # JPA entities
│       ├── exception/       # Custom exceptions + global handler
│       ├── repository/      # Spring Data repositories
│       ├── security/        # JWT service, filter, UserDetailsService
│       └── service/         # Business logic
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── .env.example
│   └── src/
│       ├── components/      # Navbar, PollCard, ProtectedRoute
│       ├── pages/           # Register, Login, Dashboard, etc.
│       ├── services/        # Axios API calls
│       └── utils/           # Auth helpers
├── docker-compose.yml
├── API.md
├── README.md
└── .gitignore
```

---

## 📋 Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi) (or use `mvnw`)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **MySQL 8** — via Docker (recommended) or [local install](https://dev.mysql.com/downloads/)
- **Docker** (optional) — [Download](https://www.docker.com/products/docker-desktop/)
- **Git** — [Download](https://git-scm.com/)

---

## 🚀 Local Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd polling-app
```

### 2. Database Setup

**Option A: Docker (Recommended)**
```bash
docker compose up -d
```
This starts MySQL on port 3306 with database `poll_db`, user `root`, password `root`.

**Option B: Local MySQL**
1. Install MySQL 8
2. Open MySQL Workbench or command line
3. Create the database:
```sql
CREATE DATABASE poll_db;
```

### 3. Backend Setup

```bash
cd backend
```

Create a `.env` file (or set environment variables):
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=poll_db
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=dGVzdFNlY3JldEtleVRoYXRJc0xvbmdFbm91Z2hGb3JITUFDU0hBMjU2QWxnb3JpdGhtMTIzNDU2
JWT_EXPIRATION=86400000
FRONTEND_URL=http://localhost:5173
```

> **Note:** Spring Boot reads these from system env vars or you can pass them via command line. The `application.properties` uses `${DB_HOST:localhost}` syntax with defaults.

Run the backend:
```bash
# Using Maven wrapper (if available)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

Verify: Open `http://localhost:8080/api/health`
```json
{"status":"UP","message":"Polling API is running"}
```

### 4. Frontend Setup

```bash
cd frontend
npm install
```

Create `.env` file:
```env
VITE_API_URL=http://localhost:8080/api
```

Run the frontend:
```bash
npm run dev
```

Open: **http://localhost:5173**

---

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn clean test
```

Tests use H2 in-memory database (no MySQL required).

### Manual Test Checklist

1. ✅ Open http://localhost:5173
2. ✅ Register user: `john@test.com` / `password123`
3. ✅ See success notification → redirected to login
4. ✅ Login with same credentials
5. ✅ See dashboard (empty initially)
6. ✅ Navigate to "Create Poll"
7. ✅ Create a poll with 3 options, future expiration
8. ✅ See success → redirected to dashboard
9. ✅ See your poll on dashboard
10. ✅ Logout → Register second user: `jane@test.com`
11. ✅ Login as Jane → See the poll
12. ✅ Vote on the poll → See results with percentages
13. ✅ Try voting again → See error (duplicate)
14. ✅ Like the poll → Like count updates
15. ✅ Click "View Details" → See poll details page
16. ✅ Add a comment → Comment appears
17. ✅ Like again → Unlike (toggle)
18. ✅ Login as John → Navigate to "My Polls"
19. ✅ Delete the poll → Confirmation dialog → Poll removed
20. ✅ Create a poll with 1-minute expiration → Wait → Try voting → Rejected

---

## 🏗️ Production Build

### Backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/polling-app-backend-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build
```
Output in `frontend/dist/` — ready for static hosting.

---

## 🌐 Deployment

### Deployment Sequence

```
1. Push to GitHub
2. Deploy MySQL on Railway
3. Deploy Backend on Railway
4. Test backend health endpoint
5. Deploy Frontend on Vercel
6. Update CORS (FRONTEND_URL)
7. Update frontend API URL (VITE_API_URL)
8. Redeploy & final test
```

### Step 1: GitHub Setup

```bash
git init
git add .
git commit -m "Initial polling app"
git branch -M main
git remote add origin https://github.com/<your-username>/polling-app.git
git push -u origin main
```

### Step 2: Railway MySQL

1. Go to [railway.app](https://railway.app/) → Sign up / login
2. New Project → Add Service → **MySQL**
3. Once deployed, click MySQL service → **Variables** tab
4. Note these values:
   - `MYSQL_HOST`
   - `MYSQL_PORT`
   - `MYSQL_DATABASE`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`

### Step 3: Railway Backend

1. In the same Railway project → **New Service** → **GitHub Repo**
2. Select your `polling-app` repository
3. Go to **Settings**:
   - Root Directory: `backend`
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/polling-app-backend-1.0.0.jar`
4. Go to **Variables** → Add:
   ```
   DB_HOST=<MYSQL_HOST from step 2>
   DB_PORT=<MYSQL_PORT from step 2>
   DB_NAME=<MYSQL_DATABASE from step 2>
   DB_USERNAME=<MYSQL_USER from step 2>
   DB_PASSWORD=<MYSQL_PASSWORD from step 2>
   JWT_SECRET=<generate a long random base64 string>
   JWT_EXPIRATION=86400000
   FRONTEND_URL=https://your-app.vercel.app
   PORT=8080
   ```
5. Go to **Settings** → **Networking** → Generate Domain
6. Your backend URL: `https://<generated>.railway.app`
7. Test: `https://<generated>.railway.app/api/health`

### Step 4: Vercel Frontend

1. Go to [vercel.com](https://vercel.com/) → Sign up / login
2. **Import** → Select your GitHub repo
3. Configure:
   - Root Directory: `frontend`
   - Framework Preset: Vite
   - Build Command: `npm run build`
   - Output Directory: `dist`
4. Environment Variables:
   ```
   VITE_API_URL=https://<your-railway-backend>.railway.app/api
   ```
5. Deploy
6. Your frontend URL: `https://<your-app>.vercel.app`

### Step 5: Update CORS

Go back to Railway backend → Variables:
```
FRONTEND_URL=https://<your-app>.vercel.app
```
Railway auto-redeploys on variable changes.

### Step 6: Vercel SPA Routing

Create `frontend/vercel.json`:
```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/index.html" }]
}
```
Push to GitHub → Vercel auto-redeploys.

---

## ⚠️ Free Tier Warnings

| Service | Free Tier Status | Limitations |
|---------|-----------------|-------------|
| **Railway** | $5 free trial credit (one-time) | No permanent free tier. Services stop when credits exhaust. Good for learning/demos. |
| **Vercel** | Free for personal projects | 100 GB bandwidth/month, serverless function limits |
| **GitHub** | Free | Unlimited public repos |

### Alternatives if Railway Credits Exhaust
- **[Render](https://render.com/)** — Free tier for web services (spins down after 15 min inactivity)
- **[Neon](https://neon.tech/)** — Free PostgreSQL (would require switching from MySQL)
- **[PlanetScale](https://planetscale.com/)** — Check current free tier availability
- **[Fl0](https://www.fl0.com/)** — Free tier for small projects

> **Honest Note:** Truly free Java backend hosting is rare. Most services offer trial credits. For a student portfolio, deploying temporarily for demos/interviews is the practical approach.

---

## 🔧 Troubleshooting

### CORS Error
- Verify `FRONTEND_URL` env var matches your Vercel URL exactly (no trailing slash)
- Check browser console for the exact origin being blocked
- Redeploy backend after changing CORS

### 401 Unauthorized
- Token may be expired — re-login
- Check that `Authorization: Bearer <token>` header is being sent
- Verify JWT_SECRET is the same between token generation and validation

### 403 Forbidden
- You're trying to delete someone else's poll
- Endpoint requires authentication but no token provided

### Database Connection Error
- Verify MySQL is running: `docker compose ps`
- Check DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD
- For Railway: Use Railway-provided connection variables

### Port Conflict
- Backend default: 8080. Change with `PORT` env var
- Frontend default: 5173. Change in `vite.config.js`

### Vite API URL Not Working
- Must use `VITE_` prefix for Vite env vars
- Restart dev server after changing `.env`
- Check `VITE_API_URL` doesn't have trailing slash

### Railway Build Failure
- Check that Root Directory is set to `backend`
- Verify `pom.xml` has correct Java version (17)
- Check build logs for specific Maven errors

### Vercel Routing (404 on refresh)
- Add `vercel.json` with SPA rewrite rule (see deployment section)
- Push and redeploy

---

## 🎓 Interview Explanation

### Architecture
> "This is a full-stack polling application with a React frontend communicating via REST API with a Spring Boot backend. The backend uses Spring Data JPA with MySQL. Authentication is stateless using JWT tokens."

### JWT Authentication Flow
> "On login, the backend validates credentials against BCrypt-hashed passwords, generates a JWT with the user's email as subject and configurable expiration, and returns it. The frontend stores the JWT in a cookie and attaches it as a Bearer token on every API request via an Axios interceptor. The backend has a JWT authentication filter that intercepts every request, extracts and validates the token, and sets the SecurityContext."

### Voting Flow
> "When a user votes: (1) authenticate via JWT, (2) find the poll and verify it exists, (3) find the option and verify it belongs to that poll, (4) check the poll hasn't expired, (5) check the user hasn't already voted via a unique constraint on user_id+poll_id, (6) create the vote record, (7) increment option vote count and poll total vote count, (8) all within a @Transactional method for atomicity."

### Database Relationships
> "Users have one-to-many relationships with polls, votes, likes, and comments. Polls have one-to-many with options, votes, likes, and comments. Votes have a unique constraint on (user_id, poll_id) to prevent duplicate voting at the database level. Likes have the same constraint for duplicate prevention."

### Why DTOs?
> "DTOs prevent exposing JPA entities directly, which avoids infinite recursion from bidirectional relationships, prevents leaking sensitive data like passwords, and gives control over the exact API contract independent of the database schema."

### How Duplicate Voting is Prevented
> "Three layers: (1) Frontend disables the vote button after voting, (2) Backend service checks `voteRepository.existsByUserIdAndPollId()` before creating a vote, (3) Database has a unique constraint on (user_id, poll_id) as the ultimate safety net."

### How Expiration Works
> "Each poll has an `expiredAt` timestamp. The `Poll.isExpired()` method compares it against the current time. The backend rejects votes on expired polls with a 400 error. The frontend shows results instead of voting controls for expired polls."

### Deployment Architecture
> "The React app is deployed as a static site on Vercel, which serves the built assets via CDN. The Spring Boot backend runs on Railway with a Railway-managed MySQL instance. The frontend communicates with the backend via HTTPS REST API calls with the backend URL configured through environment variables."

---

## 🚀 Future Improvements

- [ ] WebSocket for real-time vote updates
- [ ] Poll categories and search/filter
- [ ] User profile page with avatar upload
- [ ] Email verification on registration
- [ ] Password reset functionality
- [ ] Poll sharing with social media
- [ ] Admin dashboard
- [ ] Pagination for polls and comments
- [ ] Rate limiting on API endpoints
- [ ] Flyway database migrations for production
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Unit tests for frontend components
