# Step 1: Build
FROM gradle:8.5.0-jdk21 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

# Step 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]