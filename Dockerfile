FROM eclipse-temurin:21-jre
ENV TZ=Asia/Seoul
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]