FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app/image-service
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app/image-service
COPY --from=build /app/image-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"] 