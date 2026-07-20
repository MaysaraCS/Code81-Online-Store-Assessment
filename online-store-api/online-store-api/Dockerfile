# ---- Build stage ----
# Compiles the jar inside a container that already has Maven + JDK 21, so you
# don't need Maven installed locally just to build a Docker image.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first and download dependencies before copying source code.
# Docker caches each layer - as long as pom.xml doesn't change, this
# dependency download step is skipped on rebuilds, which is the difference
# between a 2-minute rebuild and a 20-second one while you're iterating.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# The image always builds from application.yml.example, never from a
# developer's real (gitignored, secret-containing) application.yml. All the
# actual values - DB host, JWT secret, Stripe keys - are supplied as
# environment variables by docker-compose.yml at container start, overriding
# the ${VAR:default} placeholders already in the example file. This is the
# same reason that file has env-var placeholders in the first place.
RUN cp src/main/resources/application.yml.example src/main/resources/application.yml
RUN mvn clean package -DskipTests -B

# ---- Run stage ----
# A separate, much smaller image - only a JRE (no Maven, no source code, no
# build cache) plus the one jar file copied out of the build stage above.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
