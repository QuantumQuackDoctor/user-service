FROM adoptopenjdk:16-jre-openj9 

WORKDIR /app

RUN ls

COPY /users-api/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]