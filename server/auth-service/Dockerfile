FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# When building from parent directory with this dockerfile
# Copy parent pom.xml to root level
COPY pom.xml .

# Copy service-specific pom.xml to service subdirectory
COPY auth-service/pom.xml ./auth-service/

RUN mvn -f auth-service/pom.xml dependency:go-offline

# Copy service source code
COPY auth-service/src ./auth-service/src/

RUN mvn -f auth-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"] 