# Stage 1: Build the Maven application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production JRE runtime with OpenPDF font support
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/resume-builder-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xms64m", "-Xmx384m", "-Dserver.port=8080", "-jar", "app.jar"]
