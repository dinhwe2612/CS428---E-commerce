FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml from parent directory (for building from service directory)
COPY ../pom.xml ./pom.xml

# Copy current service pom.xml
COPY pom.xml ./catalog-service/pom.xml

RUN mvn -f catalog-service/pom.xml dependency:go-offline

# Copy service source code
COPY src ./catalog-service/src/

RUN mvn -f catalog-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/catalog-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"] 