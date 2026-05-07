# Projet-RaPizz

## Prérequis:

- Java JDK 17 ou +
- Docker

## Arborescence 
```bash
.
├───RaPizz
│   ├───.settings
│   ├───bin
│   │   └───fr
│   │       └───rapizz
│   │           ├───dao
│   │           ├───main
│   │           ├───model
│   │           └───view
│   └───src
│       └───fr
│           └───rapizz
│               ├───dao
│               ├───main
│               ├───model
│               └───view
├───sql
└───sql_schema
```

RaPizz -> contient le projet Eclipse 
sql -> contient les différents scripts de lancement et initialisation de la BDD
sql_schema -> contient les schémas de base de données et les fichiers looping.  


## Démarrage

Pour démarrer le projet il faut d'abord démarrer la base de données puis le logiciel java.

### Démarrer la base de données :

```bash
sql\script
```
- script.sh -> pour linux \
- script.bat -> pour windows 

### Démarrer le projet eclipse

Eclipse -> Import projet -> Dossier RaPizz -> Import Selected

Une fois le projet importé sur eclipse, on lance App.java dans le package main.