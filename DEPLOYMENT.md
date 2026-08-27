# Polling App - Free Deployment Guide

This guide will walk you through the easiest, **100% free** method to deploy your application so you can share it with others or use it for your portfolio.

## Architecture
- **Database**: Neon (Free PostgreSQL)
- **Backend (Spring Boot)**: Render (Free Web Service)
- **Frontend (React)**: Vercel (Free Static Site)

---

## Step 1: Set up the Database (Neon)
Since the `H2` database wipes itself every time the server restarts, you need a persistent database for production.

1. Go to [Neon.tech](https://neon.tech/) and sign up for a free account.
2. Click **New Project** and name it `polling-db`. (Select Postgres 15+).
3. Once created, you will be given a **Connection String** (it looks like `postgresql://username:password@hostname/dbname`).
4. **Copy this string and save it for Step 2.**

---

## Step 2: Deploy the Backend (Render)
1. Go to [Render.com](https://render.com/) and sign in with your GitHub account.
2. Click **New** -> **Web Service**.
3. Select **Build and deploy from a Git repository** and connect your `PollingApp` repository.
4. Fill in the following details:
   - **Name**: `polling-app-backend`
   - **Root Directory**: `backend` (Important!)
   - **Environment**: `Docker`
   - **Instance Type**: `Free`
5. Scroll down to **Environment Variables** and click **Add Environment Variable**. Add the following:
   - `DB_HOST` : The hostname from your Neon connection string (e.g., `ep-little-cherry-...neon.tech`)
   - `DB_PORT` : `5432`
   - `DB_NAME` : `neondb` (or whatever the dbname is in your connection string)
   - `DB_USERNAME` : The username from your Neon string.
   - `DB_PASSWORD` : The password from your Neon string.
   - `JWT_SECRET` : Write a long random string of letters and numbers (at least 32 characters).
   - `FRONTEND_URL` : We will update this in Step 4, for now put `*`
   - `spring_datasource_url` : `jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=require` (This forces Spring to use Postgres instead of MySQL).
   - `spring_datasource_driver-class-name` : `org.postgresql.Driver`
6. Click **Create Web Service**.
7. Wait 5-10 minutes for it to build and deploy. Once live, copy the URL (e.g., `https://polling-app-backend.onrender.com`).

---

## Step 3: Deploy the Frontend (Vercel)
1. Go to [Vercel.com](https://vercel.com/) and sign in with GitHub.
2. Click **Add New** -> **Project**.
3. Import your `PollingApp` repository.
4. Click **Edit** next to the Root Directory, and select `frontend`.
5. Expand the **Environment Variables** section and add:
   - **Name**: `VITE_API_URL`
   - **Value**: Paste your Render backend URL followed by `/api` (e.g., `https://polling-app-backend.onrender.com/api`)
6. Click **Deploy**.
7. Wait 1-2 minutes. Once deployed, copy your new Vercel frontend URL.

---

## Step 4: Finalize CORS (Render)
1. Go back to Render, and open your `polling-app-backend` Web Service.
2. Go to the **Environment** tab.
3. Edit the `FRONTEND_URL` variable, and change it from `*` to your new Vercel URL (e.g., `https://polling-app-vercel.app`). Do not include a trailing slash.
4. Render will automatically restart your backend.

**🎉 Congratulations! Your full-stack Polling App is now live on the internet!**
