# Portfolio Backend

Spring Boot API behind [hariomvermas' portfolio](https://github.com/hariomvermas/Portfolio).
Java 22 · Spring Boot 4.1.2 · MySQL · Maven wrapper (no global Maven needed).

## What it serves

| Endpoint | Method | Purpose |
|---|---|---|
| `/api/contact` | `POST` | Save a contact-form message. Body: `{ "senderName", "email", "message" }`. Returns `201` + saved message (without the body text) + `Location` header. |
| `/api/contact/health` | `GET` | Liveness + `messagesStored` count. |

Validation is server-side (`@NotBlank`, `@Email`, `@Size(max=120)`). A bad
request returns `400` with a per-field error map the frontend renders under
each input.

## Run it locally

1. **MySQL** — either your native MySQL on 3306, or the Docker dev DB:
   ```powershell
   docker compose up -d        # maps host 3308 → container 3306
   $env:DB_PORT = "3308"
   ```
2. **Credentials** — the app reads `DB_PASSWORD` from the environment
   (no default — it refuses to start with a guessed password):
   ```powershell
   setx DB_PASSWORD "yourpassword"     # permanent, affects NEW windows
   $env:DB_PASSWORD = "yourpassword"   # or: this window only
   ```
   In IntelliJ: Run → Edit Configurations → Environment variables.
3. **Start** — `.\mvnw.cmd spring-boot:run` → `http://localhost:8080`
4. **Smoke test**:
   ```powershell
   curl http://localhost:8080/api/contact/health
   ```

The database `portfolio_db` and the `contact_messages` table are created
automatically (`createDatabaseIfNotExist=true`, `ddl-auto=update`).

## Tests

```powershell
.\mvnw.cmd test
```

Five tests, deliberately DB-free: bean-validation rules and service save
logic (Mockito + `ArgumentCaptor`), so `mvn test` runs anywhere without a
database.

## Live deployment (current)

- **Backend:** https://portfolio-backend-cyqz.onrender.com (Render free
  tier, Docker deploy from this repo — pushes to `main` auto-deploy)
- **MySQL:** Aiven free plan (host/port/credentials live in Render's
  environment variables only — never in this repo)
- **Frontend wiring:** the portfolio repo's `vercel.json` rewrites
  `/api/*` to the Render host, so the contact form calls same-origin URLs.

Free-tier realities: Render sleeps after ~15 min idle (first request after
a wake takes ~50 s — one retry may be needed); Aiven free plan is 1 vCPU /
1 GB and pauses on heavy use.

## Deploy — Render (free) + Aiven MySQL (free)

The `Dockerfile` builds a fat JAR and runs it on a JRE-only image; Render
detects it automatically.

1. **MySQL on [aiven.io](https://aiven.io)** → Create service → MySQL →
   free plan. Wait for RUNNING, then in the service console create a
   database named `portfolio_db` and copy the connection details.
2. **[render.com](https://render.com)** → New → Web Service → connect this
   GitHub repo (Docker is detected) → Instance type **Free** → Health check
   path `/api/contact/health` → Environment variables:
   `DB_HOST`, `DB_PORT`, `DB_NAME=portfolio_db`, `DB_USERNAME`,
   `DB_PASSWORD`, `DB_SSL=true` (Aiven requires TLS), and
   `APP_CORS_ALLOWED_ORIGINS=https://portfolio-two-iota-q72i5n608f.vercel.app`.
3. When the logs say "Started PortfolioBackendApplication", open
   `https://<your-render-host>/api/contact/health` — expect JSON status.
4. Point the frontend at it: replace `YOUR-BACKEND-HOST` in the frontend
   repo's `vercel.json` and push (Vercel redeploys automatically).

Free-tier notes: Render sleeps after ~15 min idle — the first request
after waking takes ~50 s, longer than the contact form's 10 s timeout, so
one retry may be needed. Aiven's free plan keeps credentials in its
console only — never commit them.

## Note

`docker-compose.yml` contains a **local dev** MySQL password
(`portfolio_root_pw`). It exists only inside your machine's Docker volume —
never reuse it for a real/production database.
