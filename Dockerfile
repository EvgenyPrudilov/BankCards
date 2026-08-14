FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY target/*.jar app.jar
#COPY src/main/resources/keystore.p12 /app/resources/keystore.p12

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]