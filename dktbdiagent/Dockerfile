FROM maven:3.8.3-openjdk-16 AS build
COPY . .
WORKDIR /dktbdiagent
ARG DKT_SECRET
ARG DKT_SERVER_WEB
ARG DKT_RASA_AUTH
ARG DKT_SERVER_DB
ARG DKT_DB_USER
ARG DKT_DB_PASSWORD
RUN DKT_SECRET=${DKT_SECRET} \
  DKT_SERVER_WEB=${DKT_SERVER_WEB} \
  DKT_RASA_AUTH=${DKT_RASA_AUTH} \
  DKT_SERVER_DB=${DKT_SERVER_DB} \
  DKT_DB_USER=${DKT_DB_USER} \
  DKT_DB_PASSWORD=${DKT_DB_PASSWORD} \
  mvn clean package -Dmaven.test.skip


FROM openjdk:16 AS run
WORKDIR /dktbdiagent
COPY /dktbdiagent/files /dktbdiagent/files
COPY ../dktrasa/data/nlu.yml /dktrasa/data/nlu.yml
COPY --from=build /dktbdiagent/target/agent-0.0.1-SNAPSHOT.jar agent-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "agent-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080