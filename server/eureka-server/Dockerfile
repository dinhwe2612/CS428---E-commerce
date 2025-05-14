FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY eureka-server/pom.xml ./eureka-server/

RUN mvn -f eureka-server/pom.xml dependency:go-offline

COPY eureka-server/src ./eureka-server/src/

RUN mvn -f eureka-server/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"] 