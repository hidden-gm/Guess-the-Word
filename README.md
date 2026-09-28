# WORD//TRACE — Guess the Word

A full-stack implementation of the **Guess the Word** project specification.

## Stack

- **Backend:** Spring Boot 3.5.16, Spring Web, Spring Data JPA, Spring Security, JWT, H2, Java 21
- **Frontend:** React 19 + Vite 8.1
- **Database:** H2 file database (persistent between restarts)

## Features implemented

### Player

- Register with username validation: 5–40 letters only.
- Password validation: minimum 5 characters, at least one letter, one number, and `$`, `%`, or `*`.
- JWT login and role-based authorization.
- Start/resume a round.
- Maximum 3 started rounds per day.
- Random five-letter target from the database.
- Maximum 5 guesses per round.
- Exact match = green.
- Correct letter in another position = orange.
- Letter absent = grey.
- Correct guess ends the round with a congratulatory message.
- Fifth failed guess ends the round with “BETTER LUCK NEXT TIME!”.
- Previous guesses and tile results persist in the database.

### Admin

- Seeded admin login: `admin` / `Admin1*`
- Daily report: distinct users + number of games won.
- User report: date + words tried + correct guesses.
- Word bank management: add/delete five-letter words.

## Run backend

From `backend/`:

```bash
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`.

The H2 console is also enabled at `http://localhost:8080/h2-console`.
Use JDBC URL `jdbc:h2:file:./data/guessword`, user `sa`, and a blank password.

## Run frontend

From `frontend/`:

```bash
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`.

For another backend URL, create `frontend/.env`:

```text
VITE_API_URL=http://localhost:8080/api
```

## REST API

### Auth

`POST /api/auth/register`

```json
{"username":"player","password":"Alpha1*"}
```

`POST /api/auth/login`

```json
{"username":"player","password":"Alpha1*"}
```

### Player

`GET /api/game/status`

`POST /api/game/start`

`GET /api/game/{gameId}`

`POST /api/game/{gameId}/guess`

```json
{"guess":"HOUSE"}
```

### Admin

`GET /api/admin/reports/day?date=2026-09-22`

`GET /api/admin/reports/user/{username}`

`GET /api/admin/words`

`POST /api/admin/words`

```json
{"value":"STONE"}
```

`DELETE /api/admin/words/{id}`

## Project structure

```text
guess-word-game/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/guesswordgame/
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── repository/
│       ├── security/
│       └── service/
└── frontend/
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── components/
        ├── pages/
        ├── services/
        └── styles.css
```

## Notes

The target word is never returned by the player endpoints, so the frontend cannot simply inspect the API response to reveal the answer. The game evaluation and daily quota are enforced by the backend.

For production deployment, replace the development JWT secret and H2 configuration with environment-backed secrets and a production database.
