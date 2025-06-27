FROM openjdk:17
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
COPY target/social-network-0.0.1-SNAPSHOT.jar /app/social-network.jar
ENTRYPOINT ["java", "-jar", "social-network.jar"]