# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build
# Uses a full JDK + Maven image to compile and package the application.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy only the POM first to leverage Docker layer caching for dependencies.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build, skipping tests (tests are run in CI, not at image build time).
COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime
# Minimal JRE-only image — no build tools, significantly smaller surface area.
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Security: run as a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the packaged jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Ensure the non-root user owns the workdir
RUN chown -R appuser:appgroup /app

USER appuser

# Application port
EXPOSE 8080

# JVM flags: prefer container memory limits, avoid long GC pauses
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

