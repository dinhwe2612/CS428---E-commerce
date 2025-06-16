FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml from parent directory (for building from service directory)
COPY ../pom.xml ./pom.xml

# Copy current service pom.xml
COPY pom.xml ./gateway/pom.xml

RUN mvn -f gateway/pom.xml dependency:go-offline

# Copy service source code
COPY src ./gateway/src/

RUN mvn -f gateway/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/gateway/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"] 