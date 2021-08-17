FROM adoptopenjdk:16-jre-openj9 

WORKDIR /app
RUN tree -a ./GFG 

COPY /app/users-api/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]