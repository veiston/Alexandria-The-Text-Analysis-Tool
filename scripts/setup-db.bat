rem Windows
rem Setup database script
@echo off

cd /d "%~dp0.."
mariadb -u root -p < "database\setup.sql"
