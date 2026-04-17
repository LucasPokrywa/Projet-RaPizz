
/* Requete recuperant les vehicule n'ayant jamais servis  */
SELECT * FROM Vehicule 
WHERE id_vehicule NOT IN (SELECT DISTINCT id_vehicule FROM Vente);


/* Calcul du nombre de commande par client */
SELECT c.nom_client, c.prenom_client, COUNT(v.id_vent) AS nombre_de_commandes
FROM Client c
LEFT JOIN Vente v ON c.id_client = v.id_client
GROUP BY c.id_client;

/* Calcul de la moyenne de commandes */
SELECT AVG(nb_commandes) AS moyenne_commandes
FROM (
    SELECT COUNT(id_vente) AS nb_commandes
    FROM Vente
    GROUP BY id_client
) AS stats_commandes;

/* Client ayant commandé plus que la moyenne */
SELECT c.nom_client, c.prenom_client, COUNT(v.id_vent) AS total_commandes
FROM Client c
JOIN Vente v ON c.id_client = v.id_client
GROUP BY c.id_client
HAVING COUNT(v.id_vent) > (
    SELECT AVG(nb_commandes) 
    FROM (
        SELECT COUNT(id_vente) AS nb_commandes 
        FROM Vente 
        GROUP BY id_client
    ) AS moyenne_table
);