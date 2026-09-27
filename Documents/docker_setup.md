# Docker setup

1. Open Docker Desktop.
2. Open a terminal in the project folder.

## Build the image

On a Mac with Apple Silicon:

```bash
docker build --platform linux/amd64 -f docker/Dockerfile -t alexandria:1.0.0 .
```

On Windows PowerShell:

```powershell
docker build -f docker/Dockerfile -t alexandria:1.0.0 .
```

After the build finishes, find `alexandria:1.0.0` in Docker Desktop -> Images.

## Run tests in the image

On a Mac with Apple Silicon:

```bash
docker run --platform linux/amd64 --rm alexandria:1.0.0 mvn test -Pdocker
```

On Windows PowerShell:

```powershell
docker run --rm alexandria:1.0.0 mvn test -Pdocker
```

`BUILD SUCCESS` means that the Docker test run passed.

A basic Docker container has no graphical screen or MariaDB database. For this reason, the `docker` profile in `pom.xml` excludes JavaFX view tests and database tests for now. Jenkins runs the full test suite.
