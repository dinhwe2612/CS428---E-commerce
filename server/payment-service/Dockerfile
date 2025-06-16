FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
COPY payment-service/pom.xml ./payment-service/

RUN mvn -f payment-service/pom.xml dependency:go-offline

COPY payment-service/src ./payment-service/src/

RUN mvn -f payment-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"] 