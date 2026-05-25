package fr.rapizz.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO gérant la création d'une vente et la clôture (enregistrement livraison).
 */
public class VenteDAO {

    /**
     * Crée une nouvelle vente en base.
     * Attribue automatiquement un véhicule disponible.
     * Vérifie le solde client et gère la fidélité (pizza offerte toutes les 10).
     *
     * @return l'id_vente inséré, ou -1 en cas d'échec.
     */
    public int passerCommande(int idClient, int idLivreur, int idPizza,
                               String taille, double prixCalcule) {

        // 1. Trouver un véhicule disponible
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        fr.rapizz.model.Vehicule vehicule = vehiculeDAO.getVehiculeDisponible();

        if (vehicule == null) {
            System.err.println("Aucun véhicule disponible pour le moment.");
            return -1;
        }

        // 2. Vérifier le solde (sauf si pizza offerte fidélité)
        ClientDAO clientDAO = new ClientDAO();
        int nbPizzas = clientDAO.recupererNbPizzaCommandees(idClient);
        boolean offerteFidelite = (nbPizzas > 0 && nbPizzas % 10 == 0);
        double montantFacture = offerteFidelite ? 0.0 : prixCalcule;

        if (montantFacture > 0 && clientDAO.obtenirSolde(idClient) < montantFacture) {
            System.err.println("Solde insuffisant.");
            return -1;
        }

        // 3. Insérer la vente + débiter le solde dans une transaction
        String sqlVente = """
            INSERT INTO Vente
                (date_vente, heure_commande, heure_livraison,
                 taille, offerte_fidelite, offerte_retard,
                 id_client, id_livreur, id_pizza, id_vehicule)
            VALUES (CURDATE(), CURTIME(), NULL, ?, ?, FALSE, ?, ?, ?, ?)
        """;

        String sqlDebit = "UPDATE Client SET solde_compte = solde_compte - ?, "
                        + "pizza_commande = pizza_commande + 1 WHERE id_client = ?";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idVente;

                try (PreparedStatement psVente = conn.prepareStatement(
                        sqlVente, Statement.RETURN_GENERATED_KEYS)) {

                    psVente.setString(1, taille);
                    psVente.setBoolean(2, offerteFidelite);
                    psVente.setInt(3, idClient);
                    psVente.setInt(4, idLivreur);
                    psVente.setInt(5, idPizza);
                    psVente.setInt(6, vehicule.getIdVehicule());
                    psVente.executeUpdate();

                    ResultSet keys = psVente.getGeneratedKeys();
                    if (!keys.next()) throw new SQLException("Impossible d'obtenir l'id de vente.");
                    idVente = keys.getInt(1);
                }

                // Débit
                try (PreparedStatement psDebit = conn.prepareStatement(sqlDebit)) {
                    psDebit.setDouble(1, montantFacture);
                    psDebit.setInt(2, idClient);
                    psDebit.executeUpdate();
                }

                conn.commit();
                return idVente;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Enregistre l'heure de livraison et applique la gratuité retard si > 30 min.
     * Rembourse le client si la livraison est en retard.
     *
     * @return true si la mise à jour a réussi.
     */
    public boolean enregistrerLivraison(int idVente) {
        // Récupérer l'heure de commande et le montant pour éventuel remboursement
        String sqlSelect = """
            SELECT v.heure_commande, v.date_vente, v.taille,
                   v.offerte_fidelite, v.id_client,
                   p.prix_base
            FROM Vente v
            JOIN Pizza p ON v.id_pizza = p.id_pizza
            WHERE v.id_vente = ?
        """;

        String sqlUpdate = """
            UPDATE Vente
            SET heure_livraison = CURTIME(),
                offerte_retard  = ?
            WHERE id_vente = ?
        """;

        String sqlRembourse = "UPDATE Client SET solde_compte = solde_compte + ? WHERE id_client = ?";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean retard = false;
                double montantARemb = 0.0;
                int idClient = 0;

                try (PreparedStatement psSel = conn.prepareStatement(sqlSelect)) {
                    psSel.setInt(1, idVente);
                    ResultSet rs = psSel.executeQuery();
                    if (rs.next()) {
                        // Calcul du délai en minutes depuis heure_commande
                        Timestamp commande = rs.getTimestamp("heure_commande");
                        long nowMs = System.currentTimeMillis();
                        long commandeMs = commande.getTime();
                        long diffMin = (nowMs - commandeMs) / 60000;

                        retard = diffMin > 30;
                        idClient = rs.getInt("id_client");

                        if (retard && !rs.getBoolean("offerte_fidelite")) {
                            double prixBase = rs.getDouble("prix_base");
                            String taille = rs.getString("taille");
                            montantARemb = switch (taille) {
                                case "naine"   -> prixBase * 2.0 / 3.0;
                                case "ogresse" -> prixBase * 4.0 / 3.0;
                                default        -> prixBase;
                            };
                        }
                    }
                }

                try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdate)) {
                    psUpd.setBoolean(1, retard);
                    psUpd.setInt(2, idVente);
                    psUpd.executeUpdate();
                }

                if (retard && montantARemb > 0) {
                    try (PreparedStatement psRemb = conn.prepareStatement(sqlRembourse)) {
                        psRemb.setDouble(1, montantARemb);
                        psRemb.setInt(2, idClient);
                        psRemb.executeUpdate();
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne les livraisons en cours (heure_livraison NULL).
     * Colonnes : [Livreur, Véhicule, Client, Pizza, Taille, Heure commande]
     */
    public List<String[]> getLivraisonsEnCours() {
        List<String[]> rows = new ArrayList<>();
        String sql = """
            SELECT l.prenom_livreur, l.nom_livreur,
                   ve.type_vehicule, ve.immatriculation,
                   c.prenom_client,  c.nom_client,
                   p.nom_pizza,      v.taille,
                   v.heure_commande
            FROM Vente v
            JOIN Livreur   l  ON v.id_livreur   = l.id_livreur
            JOIN Vehicule  ve ON v.id_vehicule  = ve.id_vehicule
            JOIN Client    c  ON v.id_client    = c.id_client
            JOIN Pizza     p  ON v.id_pizza     = p.id_pizza
            WHERE v.heure_livraison IS NULL
            ORDER BY v.heure_commande
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("prenom_livreur") + " " + rs.getString("nom_livreur"),
                    rs.getString("type_vehicule")  + " [" + rs.getString("immatriculation") + "]",
                    rs.getString("prenom_client")  + " " + rs.getString("nom_client"),
                    rs.getString("nom_pizza") + " (" + rs.getString("taille") + ")",
                    rs.getString("heure_commande")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * Retourne les ids des ventes en cours (pour le bouton "Livré").
     */
    public List<Integer> getIdsVentesEnCours() {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_vente FROM Vente WHERE heure_livraison IS NULL ORDER BY heure_commande";
        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ids.add(rs.getInt("id_vente"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }
}
