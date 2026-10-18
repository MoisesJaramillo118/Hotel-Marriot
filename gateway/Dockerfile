# syntax=docker/dockerfile:1.7
# API Gateway: único punto de entrada, con terminación TLS (HTTPS) y validación de JWT.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml ./pom.xml
COPY gateway/pom.xml ./gateway/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -q -f gateway/pom.xml dependency:go-offline -B || true
COPY gateway/src ./gateway/src
RUN --mount=type=cache,target=/root/.m2 mvn -q -f gateway/pom.xml package -B -DskipTests \
    && cp gateway/target/app.jar /build/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=America/Lima \
    JAVA_TOOL_OPTIONS="-Duser.timezone=America/Lima -XX:MaxRAMPercentage=75"
RUN useradd --system --uid 1001 app
# Certificado autofirmado para el entorno académico; en producción se monta uno real (SSL_KEYSTORE).
RUN keytool -genkeypair -alias gateway -keyalg RSA -keysize 2048 -validity 3650 \
      -storetype PKCS12 -keystore /app/keystore.p12 -storepass changeit \
      -dname "CN=localhost, OU=SOA, O=UTP, L=Lima, C=PE" -ext "SAN=dns:localhost,ip:127.0.0.1" \
    && chown app /app/keystore.p12
COPY --from=build /build/app.jar /app/app.jar
USER app
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
