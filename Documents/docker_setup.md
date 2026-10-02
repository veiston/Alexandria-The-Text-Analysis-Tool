# Docker setup

Run all commands from the repository root. Before starting the application,
open Docker Desktop and start an X server: XQuartz on macOS or XMing on Windows.
The X server displays the JavaFX window from the Docker container.

Set the host folder that the container may read. It appears in the file chooser
as `/host`:

On macOS or Linux:

```bash
export HOST_FILES_DIR="$HOME"
```

On Windows PowerShell:

```powershell
$env:HOST_FILES_DIR = $env:USERPROFILE
```

On macOS, allow the Docker container to connect to XQuartz before starting the
application. This temporarily disables XQuartz access control for the local
test; run `xhost -` after testing to enable it again:

```bash
xhost +
```

## 1. Build and test the application JAR

Create the executable JAR used by Docker. Maven also runs the project test
suite before creating the JAR:

```bash
mvn clean package
```

## 2. Build the local Docker image

Create the Linux AMD64 image named `alexandria:1.0.0`:

```bash
docker build --platform linux/amd64 -f docker/Dockerfile -t alexandria:1.0.0 .
```

Confirm that the local image was created:

```bash
docker image ls alexandria
```

## 3. Test the local image

Run the local image and open the Alexandria JavaFX window through the X server:

```bash
docker run --rm --platform linux/amd64 -e DISPLAY=host.docker.internal:0.0 alexandria:1.0.0
```

This command starts only the application, so it verifies that the GUI image
opens. It does not start MariaDB; use step 5 to test login and all database
features.

Close the Alexandria window to stop this container.

Re-enable XQuartz access control after the local GUI test:

```bash
xhost -
```

## 4. Push the image to Docker Hub

Sign in to the Docker Hub account that owns `ksenishl/alexandria`:

```bash
docker login
```

Give the local image the versioned Docker Hub tag and a `latest` tag:

```bash
docker tag alexandria:1.0.0 ksenishl/alexandria:1.0.0
docker tag alexandria:1.0.0 ksenishl/alexandria:latest
```

Upload both tags to Docker Hub:

```bash
docker push ksenishl/alexandria:1.0.0
docker push ksenishl/alexandria:latest
```

## 5. Verify the published image and deploy it with MariaDB

Download the versioned image from Docker Hub. This verifies that the published
image, rather than an untagged local build, can be used:

```bash
docker pull ksenishl/alexandria:1.0.0
```

Start the published Alexandria image and MariaDB. Compose starts the application
only after MariaDB passes its healthcheck. The file chooser opens at `/host`,
which is the host user folder mounted read-only in the container; choose files
from `/host/Desktop`, `/host/Documents`, or another folder under `/host`.

```bash
docker compose -f docker/docker-compose.yml up
```

Stop the deployed application and database containers:

```bash
docker compose -f docker/docker-compose.yml down
```

Remove the containers and database data when a completely fresh demonstration
database is needed:

```bash
docker compose -f docker/docker-compose.yml down -v
```
