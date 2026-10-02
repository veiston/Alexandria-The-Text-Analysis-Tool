# Docker demonstration script

Run the commands from the repository root.

## Before the demonstration

1. Start Docker Desktop.
2. On macOS, start XQuartz. In XQuartz settings, enable **Allow connections from network clients**, then quit and open XQuartz again if that setting was changed.
3. Open the Docker Hub page for `ksenishl/alexandria` in a browser. Keep it ready to show the `1.0.0` and `latest` tags.

## Demonstration

### 1. Show the Docker configuration

Open these files in the IDE:

- `docker/Dockerfile`
- `docker/docker-compose.yml`

Say:

> The Dockerfile installs Java, Linux GUI libraries, and JavaFX, then copies and starts the Alexandria JAR. Docker Compose starts the published Alexandria image and MariaDB together. The application starts only after MariaDB is healthy.

### 2. Download the published image

In Terminal, run:

```bash
docker pull --platform linux/amd64 ksenishl/alexandria:1.0.0
```

Say:

> This downloads the versioned image from our public Docker Hub repository, so this is not only a local build.

### 3. Start Alexandria and MariaDB

On macOS, run these commands:

```bash
export DISPLAY=:0
xhost +
export HOST_FILES_DIR="$HOME"
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

The Alexandria window opens through XQuartz. `docker compose ... ps` shows the running application and MariaDB containers.

Say:

> `HOST_FILES_DIR` mounts my own user folder inside the container as `/host`, so the JavaFX application can select files from my computer. MariaDB data is stored in the named Docker volume `mariadb_data`.

### 4. Show the working application

In the Alexandria window:

1. Log in with an existing account, or create an account.
2. Open the file chooser.
3. Select a file from `/host/Desktop` or `/host/Documents`.
4. Upload and analyze the file.

Say:

> The GUI runs in Docker, the file comes from the host computer through the mounted `/host` folder, and authentication uses MariaDB in the second container.

### 5. Show Docker Hub

Show the Docker Hub page for `ksenishl/alexandria` and the `1.0.0` and `latest` tags.

Say:

> The image was tagged with the team Docker Hub username and version, then pushed to Docker Hub. Jenkins builds, tests, and pushes these tags automatically.

## Only if the lecturer asks how the image is built locally

Show these commands; running them is optional if the image is already built and the demonstration is short:

```bash
mvn clean package
docker build --platform linux/amd64 -f docker/Dockerfile -t alexandria:1.0.0 .
```

To show the GUI image alone, without MariaDB:

```bash
docker run --rm --platform linux/amd64 -e DISPLAY=host.docker.internal:0.0 alexandria:1.0.0
```

This last command proves that the JavaFX image opens. Do not use it for login or file analysis because it does not start MariaDB. Use Docker Compose for the full application demonstration.

## Finish

After the demonstration, stop the containers and restore XQuartz access control:

```bash
docker compose -f docker/docker-compose.yml down
xhost -
```

Do not use `docker compose ... down -v`. The `-v` option deletes `mariadb_data`, including saved accounts and authorization data.
