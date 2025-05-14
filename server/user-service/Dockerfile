FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY user-service/pom.xml ./user-service/

RUN mvn -f user-service/pom.xml dependency:go-offline

COPY user-service/src ./user-service/src/

RUN mvn -f user-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"] 