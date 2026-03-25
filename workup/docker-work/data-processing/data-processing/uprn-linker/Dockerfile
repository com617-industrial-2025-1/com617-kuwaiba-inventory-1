# Stage 1: Build the Java application
FROM maven:3.9-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
COPY --from=build /app/target/uprn-linker-1.0.jar /app/linker.jar
ENTRYPOINT ["java", "-jar", "/app/linker.jar"]