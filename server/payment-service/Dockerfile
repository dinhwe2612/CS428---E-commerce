FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml from parent directory (for building from service directory)
COPY ../pom.xml ./pom.xml

# Copy current service pom.xml
COPY pom.xml ./payment-service/pom.xml

RUN mvn -f payment-service/pom.xml dependency:go-offline

# Copy service source code
COPY src ./payment-service/src/

RUN mvn -f payment-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/payment-service/target/*.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"] 