# pulling a base image that has maven and java 21 installed to run the app
FROM maven:3.9-eclipse-temurin-21 AS build

# sets app as the working directory
WORKDIR /app

# Copying dependencies so docker can cache the layer if they are unchanged
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# downloads all dependencies and caches them.
RUN mvn dependency:go-offline -q

# copies all source code files
COPY src/ src/

# compiles the code into a jar that goes to /app/target
RUN mvn package -DskipTests -q

# fresh image without building tools used before
FROM eclipse-temurin:21-jre

# setting the working directory in this container
WORKDIR /app

# Copy only the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# exposes api on port 8080
EXPOSE 8080

# command that runs when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]