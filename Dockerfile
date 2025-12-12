# ------------ BUILD STAGE ------------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Install SBT
ARG SBT_VERSION=1.10.0
RUN apt-get update && apt-get install -y curl && \
    curl -L -o sbt.deb https://repo.scala-sbt.org/scalasbt/debian/sbt-${SBT_VERSION}.deb && \
    apt install ./sbt.deb

COPY . .

# Compile + stage Play app
RUN sbt clean stage

# ------------ RUN STAGE ------------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/universal/stage ./stage

EXPOSE 9000

CMD ["./stage/bin/ralearn-1", "-Dplay.http.secret.key=${PLAY_SECRET_KEY}", "-Dhttp.port=9000"]