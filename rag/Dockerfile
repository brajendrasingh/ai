# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first # This improves Docker layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build Spring Boot application
RUN ./gradlew bootJar --no-daemon


# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy generated Spring Boot JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot default port
EXPOSE 8080

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]