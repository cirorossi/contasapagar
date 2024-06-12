FROM openjdk:17-jdk-slim
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src

RUN mvn package -DskipTests
COPY target/contasapagar-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

