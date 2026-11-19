@echo off
rem Stop the app and the database in Docker on Windows.

setlocal
cd /d "%~dp0.."
set "HOST_FILES_DIR=%USERPROFILE%"

docker compose -p alexandria -f docker\docker-compose.yml stop
