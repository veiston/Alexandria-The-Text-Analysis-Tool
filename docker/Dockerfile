# It says to use Maven 3.9.6 with Eclipse Temurin Java 17
FROM maven:3.9.6-eclipse-temurin-17

# It says to set /app as the working directory inside the container
WORKDIR /app

# It says to copy pom.xml into the working directory
COPY pom.xml .

# It says to copy the source code into the container
COPY src ./src

# It says to build the project with Maven
RUN mvn clean package -Pdocker

# It says to run the generated JAR when the container starts
CMD ["java", "-jar", "target/alexandria-1.0.0.jar"]
