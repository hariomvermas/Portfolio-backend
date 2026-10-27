# ── Build stage: Maven + JDK 21 ────────────────────────────────
# Full SDK only lives in this throwaway stage — the final image
# never carries the compiler or the dependency cache.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# pom.xml first, source later: Docker caches the dependency layer,
# so source-only changes rebuild without re-downloading the internet.
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
# Tests are run separately (mvnw test) — image builds stay fast.
RUN mvn -q package -DskipTests

# ── Runtime stage: JRE only, much smaller image ────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Deploy hosts assign the listen port via the PORT env var
# (Render uses 10000); locally nothing is set so it stays 8080.
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
