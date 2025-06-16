FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy parent pom.xml to root level of build context
COPY pom.xml .

# Create cart-service subdirectory and copy service pom.xml there
COPY cart-service/pom.xml ./cart-service/

RUN mvn -f cart-service/pom.xml dependency:go-offline

# Copy service source code to cart-service subdirectory
COPY cart-service/src ./cart-service/src/

RUN mvn -f cart-service/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/cart-service/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"] 