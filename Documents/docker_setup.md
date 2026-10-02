# Docker setup

Open Docker Desktop and run the commands from the repository root. Start the
X server used by your operating system first (XQuartz on macOS or XMing on
Windows).

## Build and test

Create the executable JAR that Docker copies into the application image:

```bash
mvn clean package
```

Build the Alexandria Docker image and give it the `alexandria:1.0.0` tag:

```bash
docker build -f docker/Dockerfile -t alexandria:1.0.0 .
```

Start the JavaFX application and MariaDB. The application starts after the
MariaDB healthcheck succeeds:

```bash
docker compose -f docker/docker-compose.yml up --build
```

Stop both containers:

```bash
docker compose -f docker/docker-compose.yml down
```

## Docker Hub

Log in to the team's Docker Hub account. The image will be published in the
public `ksenishl/alexandria` repository:

```bash
docker login
docker tag alexandria:1.0.0 ksenishl/alexandria:1.0.0
docker tag alexandria:1.0.0 ksenishl/alexandria:latest
docker push ksenishl/alexandria:1.0.0
docker push ksenishl/alexandria:latest
```

Create the public `alexandria` repository under the `ksenishl` account in
Docker Hub before the first push. Verify the published image:

```bash
docker pull ksenishl/alexandria:1.0.0
docker image inspect ksenishl/alexandria:1.0.0
```
