# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system appuser && useradd --system --create-home --gid appuser appuser \
    && mkdir -p /app /data && chown -R appuser:appuser /app /data
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=30s \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080 || exit 1'
