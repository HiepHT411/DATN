FROM openjdk:11
EXPOSE 8080
ADD target/hust-elearning-english-0.0.1-SNAPSHOT.jar hust-elearning-english-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/hustelearningenglish-0.0.1-SNAPSHOT.jar"]