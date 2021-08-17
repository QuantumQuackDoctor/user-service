FROM adoptopenjdk:16-jre-openj9 

ARG JAR_FILE=/target/*.jar

COPY ${JAR_FILE} app.jar

CMD ls

CMD pwd

ENTRYPOINT ["java", "-jar", "/app.jar"]