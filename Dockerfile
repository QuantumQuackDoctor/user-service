FROM adoptopenjdk:16-jre-openj9 

ARG JAR_FILE=/target/*.jar

RUN echo $(ls)

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]