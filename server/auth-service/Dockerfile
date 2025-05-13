FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY auth-service/pom.xml ./auth-service/
COPY auth-service/src ./auth-service/src/
WORKDIR /app
RUN mvn -f auth-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"] 