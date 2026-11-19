@echo off
rem Run the app in Docker on Windows. The window opens in Xming.
rem Usage: scripts\run-docker-windows.bat [--build]
rem   --build  rebuild the app image from the current code

setlocal
cd /d "%~dp0.."
set "HOST_FILES_DIR=%USERPROFILE%"

set "XMING=%ProgramFiles(x86)%\Xming\Xming.exe"
if not exist "%XMING%" set "XMING=%ProgramFiles%\Xming\Xming.exe"
tasklist /fi "imagename eq Xming.exe" | find /i "Xming.exe" >nul || start "" "%XMING%" :0 -multiwindow -clipboard -ac

docker info >nul 2>&1 || start "" "%ProgramFiles%\Docker\Docker\Docker Desktop.exe"
echo Waiting for Docker...
:wait_docker
docker info >nul 2>&1 && goto docker_ready
timeout /t 2 /nobreak >nul
goto wait_docker
:docker_ready

if "%~1"=="--build" goto build
docker image inspect alexandria:1.0.0 >nul 2>&1 && goto run
:build
call mvn clean package -Dmaven.compiler.release=17 -DskipTests || exit /b 1
docker build --platform linux/amd64 -f docker\Dockerfile -t alexandria:1.0.0 . || exit /b 1

:run
(
echo services:
echo   app:
echo     image: alexandria:1.0.0
echo     pull_policy: never
) | docker compose -p alexandria -f docker\docker-compose.yml -f - up -d
