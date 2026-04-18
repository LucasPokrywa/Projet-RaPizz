package fr.rapizz.dao;

import fr.rapizz.model.Vehicule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDAO {
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