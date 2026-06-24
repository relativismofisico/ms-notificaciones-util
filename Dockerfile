# ─────────────────────────────────────────────
# Stage 1 – Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties ./
COPY coverage.gradle test.gradle ./
COPY src/ src/

RUN chmod +x gradlew && \
    ./gradlew bootJar --no-daemon -x test

# ─────────────────────────────────────────────
# Stage 2 – Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]