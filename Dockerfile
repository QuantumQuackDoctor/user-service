FROM adoptopenjdk:16-jre-openj9 

WORKDIR /app

COPY /var/lib/jenkins/workspace/user-service-job/users-api/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]