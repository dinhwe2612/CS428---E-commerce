FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY notification-service/pom.xml ./notification-service/

RUN mvn -f notification-service/pom.xml dependency:go-offline

COPY notification-service/src ./notification-service/src/

RUN mvn -f notification-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"] 