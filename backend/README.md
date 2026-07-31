# RAFIQ Backend

Production-ready REST API + WebSocket server for the RAFIQ accessibility app.

## Tech Stack

- **Runtime:** Node.js 18+
- **Framework:** Express.js
- **Database:** PostgreSQL 14+
- **Auth:** JWT (bcryptjs + jsonwebtoken)
- **Realtime:** WebSocket (ws)
- **Security:** Helmet, CORS, Rate Limiting

## Quick Start

### 1. Install Dependencies

```bash
cd backend
npm install
```

### 2. Setup PostgreSQL

```bash
# Create database
createdb rafiq_db

# Create user
psql -c "CREATE USER rafiq_user WITH PASSWORD 'rafiq_password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE rafiq_db TO rafiq_user;"
```

### 3. Configure Environment

```bash
cp .env.example .env
# Edit .env with your database URL and JWT secret
```

### 4. Run Migrations

```bash
npm run migrate
```

### 5. Start Server

```bash
# Development
npm run dev

# Production
npm start
```

## API Endpoints

### Health
- `GET /api/health` - Server health check

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `POST /api/auth/guest` - Guest login
- `GET /api/auth/me` - Get current user profile
- `PUT /api/auth/password` - Change password
- `DELETE /api/auth/account` - Delete account

### Users
- `GET /api/users/profile` - Get full profile
- `PUT /api/users/profile` - Update profile
- `PUT /api/users/disability-type` - Update disability type (+50 points)
- `PUT /api/users/fcm-token` - Update FCM push token
- `PUT /api/users/guardian-mode` - Toggle guardian mode

### Scoreboard
- `GET /api/scoreboard` - Global leaderboard
- `GET /api/scoreboard/my-rank` - Your rank with nearby users
- `GET /api/scoreboard/history` - Your score history
- `POST /api/scoreboard/add-points` - Add points

### Places
- `GET /api/places` - List places (own + public)
- `POST /api/places` - Add place (+50 points)
- `PUT /api/places/:id` - Update place
- `DELETE /api/places/:id` - Delete place

### Contacts
- `GET /api/contacts` - List emergency contacts
- `POST /api/contacts` - Add contact
- `PUT /api/contacts/:id` - Update contact
- `DELETE /api/contacts/:id` - Delete contact

### Medications
- `GET /api/medications` - List medications
- `POST /api/medications` - Add medication
- `PUT /api/medications/:id` - Update medication
- `DELETE /api/medications/:id` - Delete medication

### Chat
- `GET /api/chat/messages` - Get chat history
- `POST /api/chat/messages` - Save message
- `POST /api/chat/sync` - Sync multiple messages
- `DELETE /api/chat/messages/:id` - Delete message

### SOS
- `POST /api/sos/alert` - Create SOS alert
- `GET /api/sos/alerts` - Get SOS history
- `GET /api/sos/active` - Get active alert
- `PUT /api/sos/alerts/:id/resolve` - Resolve alert

### Analytics
- `POST /api/analytics/event` - Log activity event
- `GET /api/analytics/stats` - Get usage statistics

## WebSocket Events

Connect to `ws://localhost:3000/ws`

### Client -> Server
- `{ type: "auth", token: "jwt..." }` - Authenticate
- `{ type: "sos_alert", latitude, longitude, targetContact }` - Trigger SOS
- `{ type: "sos_cancel" }` - Cancel SOS
- `{ type: "ping" }` - Keep alive

### Server -> Client
- `{ type: "connected" }` - Connection established
- `{ type: "auth_success", userId }` - Auth successful
- `{ type: "sos_sent", alertId }` - SOS acknowledged
- `{ type: "sos_broadcast", ... }` - SOS broadcast to guardians
- `{ type: "pong" }` - Pong response

## Database Schema

See `db/migrations/001_initial.sql` for full schema.
