package fr.rapizz.dao;

import fr.rapizz.model.Ingredient;
import fr.rapizz.model.Pizza;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaDAO {

    public List<String> getNomsSeulement() {
        List<String> noms = new ArrayList<>();
        String sql = "SELECT nom_pizza FROM Pizza";
        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) noms.add(rs.getString("nom_pizza"));
        } catch (SQLException e) { e.printStackTrace(); }
        return noms;
    }

    public List<Double> getPrixSeulement() {
        List<Double> prix = new ArrayList<>();
        String sql = "SELECT prix_base FROM Pizza";
        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) prix.add(rs.getDouble("prix_base"));
        } catch (SQLException e) { e.printStackTrace(); }
        return prix;
    }

    public List<String> getIngredientsUtilises() {
        List<String> ingredients = new ArrayList<>();
        String sql = "SELECT DISTINCT i.nom_ingredient FROM Ingredient i " +
                     "JOIN Recette r ON i.id_ingredient = r.id_ingredient";
        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ingredients.add(rs.getString("nom_ingredient"));
        } catch (SQLException e) { e.printStackTrace(); }
        return ingredients;
    }

    public List<Pizza> getMenu() {
        List<Pizza> menu = new ArrayList<>();
        String sql = "SELECT p.id_pizza, p.nom_pizza, p.prix_base, " +
                     "GROUP_CONCAT(i.nom_ingredient SEPARATOR ', ') as liste_ingredients " +
                     "FROM Pizza p " +
                     "LEFT JOIN Recette r ON p.id_pizza = r.id_pizza " +
                     "LEFT JOIN Ingredient i ON r.id_ingredient = i.id_ingredient " +
                     "GROUP BY p.id_pizza";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pizza p = new Pizza(rs.getInt("id_pizza"), rs.getString("nom_pizza"), rs.getDouble("prix_base"));
                String ing = rs.getString("liste_ingredients");
                p.setIngredients(ing != null ? List.of(ing.split(", ")) : new ArrayList<>());
                menu.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return menu;
    }

    public void creerPizza(String nom, double prix, List<Ingredient> ingredients) {
        String sqlPizza = "INSERT INTO Pizza (nom_pizza, prix_base) VALUES (?, ?)";
        String sqlRecette = "INSERT INTO Recette (id_pizza, id_ingredient) VALUES (?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psP = conn.prepareStatement(sqlPizza, Statement.RETURN_GENERATED_KEYS)) {
                psP.setString(1, nom);
                psP.setDouble(2, prix);
                psP.executeUpdate();

                ResultSet rs = psP.getGeneratedKeys();
                if (rs.next() && ingredients != null) {
                    int id = rs.getInt(1);
                    try (PreparedStatement psR = conn.prepareStatement(sqlRecette)) {
                        for (Ingredient ing : ingredients) {
                            psR.setInt(1, id);
                            psR.setInt(2, ing.getIdIngredient());
                            psR.addBatch();
                        }
                        psR.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean updateInfosBase(int idPizza, String nouveauNom, double nouveauPrix) {
        String sql = "UPDATE Pizza SET nom_pizza = ?, prix_base = ? WHERE id_pizza = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nouveauNom);
            ps.setDouble(2, nouveauPrix);
            ps.setInt(3, idPizza);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void updateIngredients(int idPizza, List<Ingredient> nouveauxIngredients) {
        String sqlDelete = "DELETE FROM Recette WHERE id_pizza = ?";
        String sqlInsert = "INSERT INTO Recette (id_pizza, id_ingredient) VALUES (?, ?)";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                    psDel.setInt(1, idPizza);
                    psDel.executeUpdate();
                }
                
                if (nouveauxIngredients != null) {
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                        for (Ingredient ing : nouveauxIngredients) {
                            psIns.setInt(1, idPizza);
                            psIns.setInt(2, ing.getIdIngredient());
                            psIns.addBatch();
                        }
                        psIns.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public boolean supprimerPizza(int idPizza) {
        String sqlRecette = "DELETE FROM Recette WHERE id_pizza = ?";
        String sqlPizza = "DELETE FROM Pizza WHERE id_pizza = ?";

        try (Connection conn = DatabaseConnexion.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psRecette = conn.prepareStatement(sqlRecette);
                 PreparedStatement psPizza = conn.prepareStatement(sqlPizza)) {

                psRecette.setInt(1, idPizza);
                psRecette.executeUpdate();

                psPizza.setInt(1, idPizza);
                int rowsAffected = psPizza.executeUpdate();

                conn.commit();
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}