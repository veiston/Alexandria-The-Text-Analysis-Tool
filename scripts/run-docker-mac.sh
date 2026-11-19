#!/bin/bash
# Run the app in Docker on macOS. The window opens in XQuartz.
# Usage: scripts/run-docker-mac.sh [--build]
#   --build  rebuild the app image from the current code

set -euo pipefail

cd "$(dirname "$0")/.."
export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"
export HOST_FILES_DIR="$HOME"
XAUTH_FILE="${TMPDIR:-/tmp}/alexandria.xauth"

# Let Docker connect to XQuartz (needed only the first time)
defaults write org.xquartz.X11 nolisten_tcp -bool false

open -a XQuartz
open -a Docker
echo "Waiting for Docker..."
until docker info >/dev/null 2>&1; do sleep 2; done

if [ "${1:-}" = "--build" ] || ! docker image inspect alexandria:1.0.0 >/dev/null 2>&1; then
    mvn clean package -Dmaven.compiler.release=17 -DskipTests
    docker build --platform linux/amd64 -f docker/Dockerfile -t alexandria:1.0.0 .
fi

# XQuartz makes a new key each time it starts. Give it to the container.
until DISPLAY=:0 /opt/X11/bin/xauth nlist :0 | grep -q .; do sleep 1; done
: > "$XAUTH_FILE"
DISPLAY=:0 /opt/X11/bin/xauth nlist :0 | sed 's/^..../ffff/' | /opt/X11/bin/xauth -f "$XAUTH_FILE" nmerge -
chmod 644 "$XAUTH_FILE"

docker compose -p alexandria -f docker/docker-compose.yml -f - up -d <<EOF
services:
  app:
    image: alexandria:1.0.0
    pull_policy: never
    environment:
      XAUTHORITY: /tmp/.xauth
    volumes:
      - ${XAUTH_FILE}:/tmp/.xauth:ro
EOF
