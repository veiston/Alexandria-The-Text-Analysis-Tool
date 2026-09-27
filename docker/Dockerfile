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

# How to use Docker
#
# 1. Open Docker Desktop. Then open a terminal in the project folder.
#    Mac with Apple Silicon:
#    docker build --platform linux/amd64 -f docker/Dockerfile -t alexandria:1.0.0 .
#
#    Windows PowerShell:
#    docker build -f docker/Dockerfile -t alexandria:1.0.0 .
#
# 2. After the build finishes, find alexandria:1.0.0 in Docker Desktop -> Images.
#
# 3. Run the tests inside the image.
#    Mac with Apple Silicon:
#    docker run --platform linux/amd64 --rm alexandria:1.0.0 mvn test -Pdocker
#
#    Windows PowerShell:
#    docker run --rm alexandria:1.0.0 mvn test -Pdocker
#
# Docker cannot show JavaFX windows, so view tests are not run in Docker.
