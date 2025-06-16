FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
COPY catalog-service/pom.xml ./catalog-service/

RUN mvn -f catalog-service/pom.xml dependency:go-offline

COPY catalog-service/src ./catalog-service/src/

RUN mvn -f catalog-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/catalog-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"] 