package fr.rapizz.dao;

import fr.rapizz.model.Pizza;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaDAO {

    public List<Pizza> getMenu() {
        List<Pizza> listePizzas = new ArrayList<>();
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
                Pizza p = new Pizza();
                p.setIdPizza(rs.getInt("id_pizza"));
                p.setNomPizza(rs.getString("nom_pizza"));
                p.setPrixBase(rs.getDouble("prix_base"));
                
                String ing = rs.getString("liste_ingredients");
                if (ing != null && !ing.isEmpty()) {
                    p.setIngredients(List.of(ing.split(", ")));
                } else {
                    p.setIngredients(new ArrayList<>()); 
                }
                
                listePizzas.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listePizzas;
    }
}