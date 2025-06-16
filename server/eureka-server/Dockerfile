FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml from parent directory (for building from service directory)
COPY ../pom.xml ./pom.xml

# Copy current service pom.xml
COPY pom.xml ./eureka-server/pom.xml

RUN mvn -f eureka-server/pom.xml dependency:go-offline

# Copy service source code
COPY src ./eureka-server/src/

RUN mvn -f eureka-server/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/eureka-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"] 