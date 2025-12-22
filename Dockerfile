FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY data ./data
COPY target/vaadin-java25-app-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]