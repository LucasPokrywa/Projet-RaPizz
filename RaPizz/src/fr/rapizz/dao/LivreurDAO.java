package fr.rapizz.dao;

import fr.rapizz.model.Livreur;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreurDAO {

    public List<Livreur> getAllLivreurs() {
        List<Livreur> livreurs = new ArrayList<>();
        String sql = "SELECT id_livreur, nom_livreur, prenom_livreur FROM Livreur";

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livreur l = new Livreur();
                l.setIdLivreur(rs.getInt("id_livreur"));
                l.setNomLivreur(rs.getString("nom_livreur"));
                l.setPrenomLivreur(rs.getString("prenom_livreur"));
                livreurs.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livreurs;
    }

    /**
     * Retourne les livreurs disponibles (non en livraison en ce moment).
     * Un livreur est occupé s'il a une Vente avec heure_livraison NULL.
     */
    public List<Livreur> getLivreursDisponibles() {
        List<Livreur> disponibles = new ArrayList<>();
        String sql = """
            SELECT id_livreur, nom_livreur, prenom_livreur
            FROM Livreur
            WHERE id_livreur NOT IN (
                SELECT DISTINCT id_livreur
                FROM Vente
                WHERE heure_livraison IS NULL
            )
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livreur l = new Livreur();
                l.setIdLivreur(rs.getInt("id_livreur"));
                l.setNomLivreur(rs.getString("nom_livreur"));
                l.setPrenomLivreur(rs.getString("prenom_livreur"));
                disponibles.add(l);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disponibles;
    }

    /**
     * Retourne le suivi complet d'activité des livreurs :
     * nb livraisons, nb retards, statut actuel.
     */
    public List<String[]> getSuiviActiviteLivreurs() {
        List<String[]> rows = new ArrayList<>();
        String sql = """
            SELECT l.prenom_livreur,
                   l.nom_livreur,
                   COUNT(v.id_vente)  AS total_livraisons,
                   SUM(CASE
                       WHEN v.heure_livraison IS NOT NULL
                        AND TIMESTAMPDIFF(MINUTE,
                              CONCAT(v.date_vente, ' ', v.heure_commande),
                              CONCAT(v.date_vente, ' ', v.heure_livraison)) > 30
                       THEN 1 ELSE 0
                   END)               AS nb_retards,
                   SUM(CASE WHEN v.heure_livraison IS NULL THEN 1 ELSE 0 END) AS en_cours
            FROM Livreur l
            LEFT JOIN Vente v ON l.id_livreur = v.id_livreur
            GROUP BY l.id_livreur
            ORDER BY total_livraisons DESC
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String statut = rs.getInt("en_cours") > 0 ? "En livraison" : "Disponible";
                rows.add(new String[]{
                    rs.getString("prenom_livreur") + " " + rs.getString("nom_livreur"),
                    String.valueOf(rs.getInt("total_livraisons")),
                    String.valueOf(rs.getInt("nb_retards")),
                    statut
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean ajouterLivreur(String nom, String prenom) {
        String sql = "INSERT INTO Livreur (nom_livreur, prenom_livreur) VALUES (?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifierLivreur(int id, String nouveauNom, String nouveauPrenom) {
        String sql = "UPDATE Livreur SET nom_livreur = ?, prenom_livreur = ? WHERE id_livreur = ?";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nouveauNom);
            pstmt.setString(2, nouveauPrenom);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
