# ====================
# Build Stage
# ====================
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# ====================
# Run Stage
# ====================
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
