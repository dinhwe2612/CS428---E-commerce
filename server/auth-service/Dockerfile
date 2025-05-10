FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app/auth-service
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app/auth-service
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"] 