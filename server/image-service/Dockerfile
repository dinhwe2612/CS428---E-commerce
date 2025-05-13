FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY image-service/pom.xml ./image-service/
COPY image-service/src ./image-service/src/
WORKDIR /app
RUN mvn -f image-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/image-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"] 