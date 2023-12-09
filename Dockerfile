FROM eclipse-temurin:21-jre

COPY target/*.jar /app/na.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/na.jar"]
