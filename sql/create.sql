CREATE DATABASE IF NOT EXISTS RaPizz;
USE RaPizz;

CREATE TABLE Client (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom_client VARCHAR(75) NOT NULL,
    prenom_client VARCHAR(75) NOT NULL,
    adresse VARCHAR(200) NOT NULL,
    telephone VARCHAR(15) NOT NULL,
    solde_compte DECIMAL(8,2) NOT NULL,
    pizza_commande INT DEFAULT 0
);

CREATE TABLE Livreur (
    id_livreur INT PRIMARY KEY AUTO_INCREMENT,
    nom_livreur VARCHAR(75) NOT NULL,
    prenom_livreur VARCHAR(75) NOT NULL
);

CREATE TABLE Pizza (
    id_pizza INT PRIMARY KEY AUTO_INCREMENT,
    nom_pizza VARCHAR(40) NOT NULL,
    prix_base DECIMAL(5,2)
);

CREATE TABLE Vehicule (
    id_vehicule INT PRIMARY KEY AUTO_INCREMENT,
    immatriculation VARCHAR(10) NOT NULL,
    type_vehicule ENUM('voiture', 'moto') NOT NULL
);

CREATE TABLE Ingredient (
    id_ingredient INT PRIMARY KEY AUTO_INCREMENT,
    nom_ingredient VARCHAR(30) NOT NULL
);

CREATE TABLE Recette (
    id_pizza INT,
    id_ingredient INT,
    PRIMARY KEY (id_pizza, id_ingredient),
    FOREIGN KEY (id_pizza) REFERENCES Pizza(id_pizza),
    FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id_ingredient)
);

CREATE TABLE Vente (
    id_vente INT PRIMARY KEY AUTO_INCREMENT,
    date_vente DATE NOT NULL,
    heure_commande TIME NOT NULL,
    heure_livraison TIME,
    taille ENUM('naine', 'humaine', 'ogresse') NOT NULL,
    offerte_fidelite BOOLEAN DEFAULT FALSE,
    offerte_retard BOOLEAN DEFAULT FALSE,

    id_vehicule INT NOT NULL,
    id_livreur INT NOT NULL,
    id_pizza INT NOT NULL,
    id_client INT NOT NULL,

    FOREIGN KEY (id_vehicule) REFERENCES Vehicule(id_vehicule),
    FOREIGN KEY (id_livreur) REFERENCES Livreur(id_livreur),
    FOREIGN KEY (id_pizza) REFERENCES Pizza(id_pizza),
    FOREIGN KEY (id_client) REFERENCES Client(id_client)
);  