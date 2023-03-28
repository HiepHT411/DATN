FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8081
ARG APP_NAME="hust-elearning-english"
ARG APP_VERSION="0.0.1"
ARG JAR_FILE="/target/${APP_NAME}-${APP_VERSION}-SNAPSHOT.jar"

COPY ${JAR_FILE} toeicapp.jar
ENTRYPOINT ["java", "-jar", "toeicapp.jar"]