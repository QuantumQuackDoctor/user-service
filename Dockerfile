FROM adoptopenjdk:16-jre-openj9 

ARG JAR_FILE=/target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
