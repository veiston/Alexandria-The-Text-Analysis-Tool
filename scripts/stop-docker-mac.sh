#!/bin/bash
# Stop the app and the database in Docker on macOS.

set -euo pipefail

cd "$(dirname "$0")/.."
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"
export HOST_FILES_DIR="$HOME"

docker compose -p alexandria -f docker/docker-compose.yml stop
