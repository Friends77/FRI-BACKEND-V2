FROM eclipse-temurin:21.0.3_9-jdk AS build
COPY . /app
WORKDIR /app


RUN ./gradlew assemble

FROM eclipse-temurin:21.0.3_9-jre AS run

ENV TZ=Asia/Seoul

COPY --from=build \
  /app/build/libs/*.jar \
  /app/app.jar

EXPOSE 8080

ENTRYPOINT java \
-Dspring.profiles.active=${APP_PHASE} \
-jar /app/app.jar
