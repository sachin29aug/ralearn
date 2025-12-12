# ------------ BUILD STAGE ------------
FROM hseeberger/scala-sbt:17.0.2_1.10.0_3.3.3 AS build
WORKDIR /app

COPY . .
RUN sbt clean stage

# ------------ RUN STAGE ------------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/universal/stage /app

EXPOSE 9000
CMD ["./bin/YOUR_APP_NAME", "-Dhttp.port=${PORT}", "-Dplay.http.secret.key=${PLAY_SECRET}"]