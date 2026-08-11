# 1. Build Stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 캐시 레이어 최적화
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon 2>/dev/null || true

# 전체 소스 복사 후 빌드
COPY . .
RUN chmod +x gradlew && \
    ./gradlew bootJar --no-daemon -x test && \
    ls -la build/libs/

# 2. Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/moadong-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]