# ------------ BUILD STAGE ------------
FROM hseeberger/scala-sbt:17.0.10_1.10.2_3.3.1 AS build

WORKDIR /app
COPY . .

RUN sbt clean stage

# ------------ RUN STAGE ------------
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/universal/stage/ ./stage/

ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 9000

CMD ./stage/bin/ralearn \
    -Dplay.server.http.address=0.0.0.0 \
    -Dhttp.port=${PORT} \
    -Dplay.http.secret.key=${PLAY_SECRET}
