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
docker pull --platform linux/amd64 ksenishl/alexandria:1.0.0
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

This command does not remove the `mariadb_data` Docker volume. Accounts,
password hashes, uploaded-file records, and other database data remain there,
so the same users can log in after the next `docker compose ... up` command.

Jenkins builds, tests, and publishes the image, but does not open the JavaFX
application. Start Compose manually on the computer where the GUI will be used;
that computer supplies `HOST_FILES_DIR`, so the file chooser can show its files.

Remove the containers and database data when a completely fresh demonstration
database is needed:

```bash
docker compose -f docker/docker-compose.yml down -v
```

Use `-v` only when the existing database must be deleted. It removes
`mariadb_data` and therefore removes all saved accounts and authorization data.

## 6. Demonstration flow

Use the following sequence to demonstrate the Docker deployment.

1. Show `docker/Dockerfile` and `docker/docker-compose.yml`. The Dockerfile
   provides Java, the Linux GUI dependencies, and JavaFX. Compose deploys the
   published application image with MariaDB and waits for the database
   healthcheck.
2. Build the JAR and local image with the commands in steps 1 and 2.
3. Run the command in step 3 to demonstrate that the JavaFX GUI image opens.
   This is a GUI-only test; it does not include MariaDB.
4. Show the `1.0.0` and `latest` tags in the public `ksenishl/alexandria`
   Docker Hub repository, then run the `docker pull` command from step 5.
5. Start the complete application with Compose and demonstrate login and file
   analysis. The Compose deployment is the full test because it includes both
   the JavaFX application and MariaDB.

### macOS

Start XQuartz before the demonstration. If its network-client setting was
changed, restart XQuartz first. Then run:

```bash
export DISPLAY=:0
xhost +
export HOST_FILES_DIR="$HOME"
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

The JavaFX window opens through XQuartz. In the file chooser, select a file
under `/host/Desktop`, `/host/Documents`, or another folder below `/host`.

### Windows

Start Xming or VcXsrv before the demonstration and allow connections from
Docker Desktop. In PowerShell, run:

```powershell
$env:HOST_FILES_DIR = $env:USERPROFILE
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

The same `/host` folder is available in the file chooser. For example,
`/host/Desktop` corresponds to the Windows user's Desktop folder.

After the demonstration, stop the containers with the `docker compose ... down`
command from step 5. Do not add `-v` unless the demonstration database must be
deleted.
