FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR 복사
COPY build/libs/calamity-0.0.1-SNAPSHOT.jar app.jar

# 기본 경로에서 Spring Boot가 읽도록 설정 포함 (JAR 내부에 이미 포함됨)
ENTRYPOINT ["java", "-jar", "app.jar"]