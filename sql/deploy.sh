#!/bin/bash

# On récupère le chemin du dossier où se trouve ce script
# Cela permet de lancer le script de n'importe où
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

CONTAINER_NAME="rapizz-db-container"
IMAGE_NAME="rapizz-db-image"

echo "--- Nettoyage complet (Conteneur et Volumes) ---"
docker stop $CONTAINER_NAME 2>/dev/null
docker rm -v $CONTAINER_NAME 2>/dev/null

echo "--- Reconstruction de l'image ---"
# On précise à docker build de regarder dans le dossier du script ($SCRIPT_DIR)
docker build -t $IMAGE_NAME "$SCRIPT_DIR"

echo "--- Lancement du conteneur RaPizz ---"
docker run -d \
  --name $CONTAINER_NAME \
  -p 3306:3306 \
  $IMAGE_NAME

echo "--- Base de données prête avec les données de insert.sql ---"