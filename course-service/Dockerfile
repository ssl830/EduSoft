FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/course-service-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
