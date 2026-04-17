
INSERT INTO Client (prenom_client, nom_client, adresse, telephone, solde_compte) 
VALUES 
    ("Charles", "Henry", "4 rue Pompidou Paris 75014", "+33456585774", 48.75),
    ("Léa", "Martin", "12 avenue de la Gare Lyon 69002", "+33612345678", 1250.50),
    ("Thomas", "Bernard", "45 rue de la République Marseille 13001", "+33623456789", 15.00),
    ("Emma", "Petit", "8 rue des Lilas Lille 59000", "+33634567890", 450.00),
    ("Lucas", "Robert", "22 boulevard Haussmann Paris 75009", "+33645678901", 3200.75),
    ("Chloé", "Richard", "3 square de la Paix Rennes 35000", "+33656789012", 0.00),
    ("Hugo", "Durand", "110 rue de Rivoli Paris 75001", "+33667890123", 75.20),
    ("Manon", "Dubois", "17 rue Saint-Michel Toulouse 31000", "+33678901234", 890.40),
    ("Nathan", "Moreau", "5 place Bellecour Lyon 69002", "+33689012345", 12.00),
    ("Jade", "Simon", "9 avenue de Verdun Nice 06000", "+33690123456", 210.95),
    ("Arthur", "Dupont", "66 rue de Bretagne Nantes 44000", "+33601234567", 5500.00);

INSERT INTO Livreur (prenom_livreur, nom_livreur) 
VALUES
    ("Abdel", "Henry"),
    ("Matteo", "Renoir"),
    ("Brad", "Pitt"),
    ("Quentin", "Dupieux");

INSERT INTO Pizza (nom_pizza, prix_base) 
VALUES 
    ("Reine", 12.80),
    ("Quatre fromages", 13.80),
    ("Cannibale", 15.20),
    ("Pistachio", 16.75);

INSERT INTO Vehicule(immatriculation, type_vehicule)
VALUES 
    ("AS-784-DP", "Scooter"),
    ("BO-081-ES", "Voiture");

INSERT INTO Ingredient(nom_ingredient)
VALUES 
    ("tomate"),
    ("jambon"),
    ("fromage"),
    ("olive"),
    ("pate");

INSERT INTO Recette (id_pizza, id_ingredient) 
VALUES 
    (1, 1), 
    (1, 2), 
    (1, 3), 
    (1, 5); 

INSERT INTO Vente (
    date_vente, heure_commande, heure_livraison, 
    taille, offerte_fidelite, offerte_retard, 
    id_client, id_livreur, id_pizza, id_vehicule
) 
VALUES (
    '2026-04-16', '19:00:00', '19:25:00', 
    'humaine', FALSE, FALSE, 
    1, 1, 1, 1
);