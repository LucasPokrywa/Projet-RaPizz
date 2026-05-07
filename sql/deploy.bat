@echo off
setlocal

REM Récupère le dossier du script
set SCRIPT_DIR=%~dp0

set CONTAINER_NAME=rapizz-db-container
set IMAGE_NAME=rapizz-db-image

echo --- Nettoyage complet (Conteneur et Volumes) ---
docker stop %CONTAINER_NAME% 2>nul
docker rm -v %CONTAINER_NAME% 2>nul

echo --- Reconstruction de l'image ---
docker build -t %IMAGE_NAME% .

echo --- Lancement du conteneur RaPizz ---
docker run -d ^
  --name %CONTAINER_NAME% ^
  -p 3306:3306 ^
  %IMAGE_NAME%

echo --- Base de donnees prete avec les donnees de insert.sql ---

pause