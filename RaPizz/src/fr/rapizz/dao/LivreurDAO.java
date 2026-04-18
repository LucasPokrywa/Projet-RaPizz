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

    public boolean ajouterLivreur(String nom, String prenom, String vehicule) {
        String sql = "INSERT INTO Livreur (nom_livreur, prenom_livreur) VALUES (?, ?, ?)";

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