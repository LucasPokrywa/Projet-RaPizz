package fr.rapizz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IngredientDAO {

    public boolean ajouterIngredient(String nom) {
        String sql = "INSERT INTO Ingredient (nom_ingredient) VALUES (?)";

        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'ingrédient : " + e.getMessage());
            return false;
        }
    }
}