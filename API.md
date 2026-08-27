# Polling App API Documentation

Base URL: `http://localhost:8080/api`

---

## Authentication

### POST `/auth/signup`
Register a new user.

**Auth Required:** No

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (201):**
```json
{
  "message": "User registered successfully"
}
```

**Error Responses:**
- `400` — Validation error (blank fields, invalid email, short password)
- `409` — Email already registered

---

### POST `/auth/login`
Authenticate and receive JWT token.

**Auth Required:** No

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "userId": 1
}
```

**Error Responses:**
- `400` — Validation error
- `401` — Invalid email or password

---

## Polls

### POST `/user/poll`
Create a new poll.

**Auth Required:** Yes (`Bearer <token>`)

**Request Body:**
```json
{
  "question": "What is your favorite programming language?",
  "options": ["Java", "Python", "JavaScript"],
  "expiredAt": "2025-12-31T23:59:59"
}
```

**Success Response (201):**
```json
{
  "id": 1,
  "question": "What is your favorite programming language?",
  "postedDate": "2025-01-15T10:30:00",
  "expiredAt": "2025-12-31T23:59:59",
  "expired": false,
  "totalVoteCount": 0,
  "creatorName": "John Doe",
  "creatorId": 1,
  "options": [
    { "id": 1, "title": "Java", "voteCount": 0, "percentage": 0.0 },
    { "id": 2, "title": "Python", "voteCount": 0, "percentage": 0.0 },
    { "id": 3, "title": "JavaScript", "voteCount": 0, "percentage": 0.0 }
  ],
  "selectedOptionId": null,
  "hasVoted": false,
  "likesCount": 0,
  "commentsCount": 0,
  "hasLiked": false
}
```

**Error Responses:**
- `400` — Validation error (blank question, fewer than 2 options, past expiration)
- `401` — Not authenticated

---

### GET `/user/poll`
Get all polls (sorted by newest first).

**Auth Required:** No (but user-specific fields like `hasVoted`, `hasLiked` require auth)

**Success Response (200):**
```json
[
  {
    "id": 1,
    "question": "What is your favorite programming language?",
    "postedDate": "2025-01-15T10:30:00",
    "expiredAt": "2025-12-31T23:59:59",
    "expired": false,
    "totalVoteCount": 5,
    "creatorName": "John Doe",
    "creatorId": 1,
    "options": [...],
    "selectedOptionId": 2,
    "hasVoted": true,
    "likesCount": 3,
    "commentsCount": 2,
    "hasLiked": true
  }
]
```

---

### GET `/user/poll/{id}`
Get detailed poll information including comments.

**Auth Required:** No (user-specific fields require auth)

**Success Response (200):**
```json
{
  "id": 1,
  "question": "...",
  "postedDate": "...",
  "expiredAt": "...",
  "expired": false,
  "totalVoteCount": 5,
  "creatorName": "John Doe",
  "creatorId": 1,
  "options": [...],
  "selectedOptionId": null,
  "hasVoted": false,
  "likesCount": 3,
  "commentsCount": 2,
  "hasLiked": false,
  "comments": [
    {
      "id": 1,
      "content": "Great poll!",
      "createdAt": "2025-01-15T12:00:00",
      "authorName": "Jane Smith",
      "authorId": 2
    }
  ]
}
```

**Error Responses:**
- `404` — Poll not found

---

### GET `/user/poll/my`
Get polls created by the current user.

**Auth Required:** Yes

**Success Response (200):** Same format as GET `/user/poll`

---

### DELETE `/user/poll/{id}`
Delete a poll (owner only).

**Auth Required:** Yes

**Success Response (200):**
```json
{
  "message": "Poll deleted successfully"
}
```

**Error Responses:**
- `401` — Not authenticated
- `403` — Not the poll owner
- `404` — Poll not found

---

## Voting

### POST `/user/poll/vote`
Vote on a poll.

**Auth Required:** Yes

**Request Body:**
```json
{
  "pollId": 1,
  "optionId": 2
}
```

**Success Response (200):** Returns updated PollResponse

**Error Responses:**
- `400` — Poll expired / Option doesn't belong to poll
- `401` — Not authenticated
- `404` — Poll or option not found
- `409` — User already voted on this poll

---

## Likes

### POST `/user/poll/{id}/like`
Toggle like on a poll (like/unlike).

**Auth Required:** Yes

**Success Response (200):** Returns updated PollResponse

**Error Responses:**
- `401` — Not authenticated
- `404` — Poll not found

---

## Comments

### POST `/user/poll/comment`
Add a comment to a poll.

**Auth Required:** Yes

**Request Body:**
```json
{
  "pollId": 1,
  "content": "Great poll!"
}
```

**Success Response (201):**
```json
{
  "id": 1,
  "content": "Great poll!",
  "createdAt": "2025-01-15T12:00:00",
  "authorName": "John Doe",
  "authorId": 1
}
```

**Error Responses:**
- `400` — Blank or too-long comment
- `401` — Not authenticated
- `404` — Poll not found

---

## Health

### GET `/health`
Health check endpoint.

**Auth Required:** No

**Success Response (200):**
```json
{
  "status": "UP",
  "message": "Polling API is running"
}
```

---

## Error Response Format

All errors follow this format:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "message": "Descriptive error message",
  "path": "/api/user/poll/vote"
}
```

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthenticated |
| 403 | Forbidden (not authorized) |
| 404 | Resource Not Found |
| 409 | Conflict (duplicate) |
| 500 | Internal Server Error |
