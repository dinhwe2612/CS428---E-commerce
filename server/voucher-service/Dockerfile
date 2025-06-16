FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml to root level of build context
COPY pom.xml .

# Create voucher-service subdirectory and copy service pom.xml there
COPY voucher-service/pom.xml ./voucher-service/

RUN mvn -f voucher-service/pom.xml dependency:go-offline

# Copy service source code to voucher-service subdirectory
COPY voucher-service/src ./voucher-service/src/

RUN mvn -f voucher-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/voucher-service/target/*.jar app.jar
EXPOSE 8071
ENTRYPOINT ["java", "-jar", "app.jar"] 