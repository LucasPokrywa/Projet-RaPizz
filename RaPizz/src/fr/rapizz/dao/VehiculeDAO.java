package fr.rapizz.dao;

import fr.rapizz.model.Vehicule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAO {

    /**
     * Retourne tous les véhicules de la flotte.
     */
    public List<Vehicule> getAllVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT id_vehicule, immatriculation, type_vehicule FROM Vehicule";

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setIdVehicule(rs.getInt("id_vehicule"));
                v.setImmatriculation(rs.getString("immatriculation"));
                v.setTypeVehicule(rs.getString("type_vehicule"));
                vehicules.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicules;
    }

    /**
     * Retourne le premier véhicule actuellement disponible,
     * c'est-à-dire non associé à une livraison en cours
     * (heure_livraison est NULL dans la table Vente).
     *
     * Logique : un véhicule est "occupé" s'il apparaît dans une Vente
     * dont heure_livraison est encore NULL (la livraison n'est pas terminée).
     *
     * @return le premier Vehicule disponible, ou null si aucun n'est libre.
     */
    public Vehicule getVehiculeDisponible() {
        String sql = """
            SELECT v.id_vehicule, v.immatriculation, v.type_vehicule
            FROM Vehicule v
            WHERE v.id_vehicule NOT IN (
                SELECT DISTINCT id_vehicule
                FROM Vente
                WHERE heure_livraison IS NULL
            )
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Vehicule v = new Vehicule();
                v.setIdVehicule(rs.getInt("id_vehicule"));
                v.setImmatriculation(rs.getString("immatriculation"));
                v.setTypeVehicule(rs.getString("type_vehicule"));
                return v;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // aucun véhicule disponible
    }

    /**
     * Retourne la liste des véhicules libres (pour affichage dans l'UI).
     */
    public List<Vehicule> getVehiculesDisponibles() {
        List<Vehicule> disponibles = new ArrayList<>();
        String sql = """
            SELECT v.id_vehicule, v.immatriculation, v.type_vehicule
            FROM Vehicule v
            WHERE v.id_vehicule NOT IN (
                SELECT DISTINCT id_vehicule
                FROM Vente
                WHERE heure_livraison IS NULL
            )
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicule v = new Vehicule();
                v.setIdVehicule(rs.getInt("id_vehicule"));
                v.setImmatriculation(rs.getString("immatriculation"));
                v.setTypeVehicule(rs.getString("type_vehicule"));
                disponibles.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disponibles;
    }

    /**
     * Retourne le suivi d'activité de chaque véhicule :
     * nombre total de livraisons effectuées et statut actuel.
     */
    public List<String[]> getSuiviActiviteVehicules() {
        List<String[]> rows = new ArrayList<>();
        String sql = """
            SELECT ve.immatriculation,
                   ve.type_vehicule,
                   COUNT(v.id_vente)                              AS total_livraisons,
                   SUM(CASE WHEN v.heure_livraison IS NULL THEN 1 ELSE 0 END) AS en_cours
            FROM Vehicule ve
            LEFT JOIN Vente v ON ve.id_vehicule = v.id_vehicule
            GROUP BY ve.id_vehicule
            ORDER BY total_livraisons DESC
        """;

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String statut = rs.getInt("en_cours") > 0 ? "En livraison" : "Disponible";
                rows.add(new String[]{
                    rs.getString("immatriculation"),
                    rs.getString("type_vehicule"),
                    String.valueOf(rs.getInt("total_livraisons")),
                    statut
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean ajouterVehicule(String immatriculation, String type) {
        String sql = "INSERT INTO Vehicule (immatriculation, type_vehicule) VALUES (?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, immatriculation);
            pstmt.setString(2, type);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du véhicule : " + e.getMessage());
            return false;
        }
    }
}
