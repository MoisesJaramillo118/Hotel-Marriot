# syntax=docker/dockerfile:1.7
# Imagen genérica de un servicio: se construye desde la raíz del repo con
#   --build-arg MODULE=services/<nombre>
FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG MODULE
WORKDIR /build
COPY pom.xml ./pom.xml
COPY ${MODULE}/pom.xml ./${MODULE}/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -q -f ${MODULE}/pom.xml dependency:go-offline -B || true
COPY ${MODULE}/src ./${MODULE}/src
RUN --mount=type=cache,target=/root/.m2 mvn -q -f ${MODULE}/pom.xml package -B -DskipTests \
    && cp ${MODULE}/target/app.jar /build/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=America/Lima \
    JAVA_TOOL_OPTIONS="-Duser.timezone=America/Lima -XX:MaxRAMPercentage=75"
RUN useradd --system --uid 1001 app
COPY --from=build /build/app.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
