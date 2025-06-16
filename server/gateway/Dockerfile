FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
COPY gateway/pom.xml ./gateway/

RUN mvn -f gateway/pom.xml dependency:go-offline

COPY gateway/src ./gateway/src/

RUN mvn -f gateway/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/gateway/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"] 