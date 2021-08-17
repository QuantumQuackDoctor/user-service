FROM adoptopenjdk:16-jre-openj9 

WORKDIR /app

COPY /users-api/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]