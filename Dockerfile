FROM adoptopenjdk:16-jre-openj9 

ARG JAR_FILE=/target/*.jar

RUN echo $(ls)
RUN echo $(ls /etc/)
RUN echo $(ls /usr/)
RUN echo $(ls /var/)

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]