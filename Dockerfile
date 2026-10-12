FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=build /workspace/target/notes-api-0.0.1-SNAPSHOT.jar /app/notes-api.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/notes-api.jar"]