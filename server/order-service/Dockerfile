FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
COPY order-service/pom.xml ./order-service/

RUN mvn -f order-service/pom.xml dependency:go-offline

COPY order-service/src ./order-service/src/

RUN mvn -f order-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]