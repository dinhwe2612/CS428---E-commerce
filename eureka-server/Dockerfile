FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app

RUN apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"] 