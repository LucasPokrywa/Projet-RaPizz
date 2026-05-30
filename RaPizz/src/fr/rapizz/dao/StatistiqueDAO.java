package fr.rapizz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatistiqueDAO {

    public double chiffreAffairesTotal() {

        String sql = """
            SELECT COALESCE(SUM(
                CASE
                    WHEN v.offerte_fidelite = TRUE
                      OR v.offerte_retard = TRUE
                    THEN 0

                    WHEN v.taille = 'naine'
                    THEN p.prix_base * 2 / 3

                    WHEN v.taille = 'humaine'
                    THEN p.prix_base

                    WHEN v.taille = 'ogresse'
                    THEN p.prix_base * 4 / 3
                END
            ), 0) AS total

            FROM Vente v
            JOIN Pizza p ON v.id_pizza = p.id_pizza
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String meilleurClient() {

        String sql = """
            SELECT c.prenom_client,
                   c.nom_client,
                   COUNT(v.id_vente) AS total

            FROM Client c
            JOIN Vente v ON c.id_client = v.id_client

            GROUP BY c.id_client

            ORDER BY total DESC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("prenom_client")
                        + " "
                        + rs.getString("nom_client")
                        + " ("
                        + rs.getInt("total")
                        + " commandes)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucun";
    }

    public String plusMauvaisLivreur() {

        String sql = """
            SELECT l.prenom_livreur,
                   l.nom_livreur,
                   COUNT(v.id_vente) AS retards

            FROM Livreur l
            JOIN Vente v ON l.id_livreur = v.id_livreur

            WHERE TIMESTAMPDIFF(
                    MINUTE,
                    CONCAT(v.date_vente, ' ', v.heure_commande),
                    CONCAT(v.date_vente, ' ', v.heure_livraison)
                  ) > 30

            GROUP BY l.id_livreur

            ORDER BY retards DESC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("prenom_livreur")
                        + " "
                        + rs.getString("nom_livreur")
                        + " ("
                        + rs.getInt("retards")
                        + " retards)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucun";
    }

    public String vehiculePlusUtilise() {

        String sql = """
            SELECT ve.type_vehicule,
                   ve.immatriculation,
                   COUNT(v.id_vente) AS total

            FROM Vehicule ve
            JOIN Vente v ON ve.id_vehicule = v.id_vehicule

            GROUP BY ve.id_vehicule

            ORDER BY total DESC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("type_vehicule")
                        + " - "
                        + rs.getString("immatriculation")
                        + " ("
                        + rs.getInt("total")
                        + " utilisations)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucun";
    }

    public String pizzaPlusDemandee() {

        String sql = """
            SELECT p.nom_pizza,
                   COUNT(v.id_vente) AS total

            FROM Pizza p
            JOIN Vente v ON p.id_pizza = v.id_pizza

            GROUP BY p.id_pizza

            ORDER BY total DESC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("nom_pizza")
                        + " ("
                        + rs.getInt("total")
                        + " commandes)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucune";
    }

    public String pizzaMoinsDemandee() {

        String sql = """
            SELECT p.nom_pizza,
                   COUNT(v.id_vente) AS total

            FROM Pizza p
            LEFT JOIN Vente v ON p.id_pizza = v.id_pizza

            GROUP BY p.id_pizza

            ORDER BY total ASC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("nom_pizza")
                        + " ("
                        + rs.getInt("total")
                        + " commandes)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucune";
    }

    public String ingredientFavori() {

        String sql = """
            SELECT i.nom_ingredient,
                   COUNT(v.id_vente) AS total

            FROM Ingredient i

            JOIN Recette r
                ON i.id_ingredient = r.id_ingredient

            JOIN Pizza p
                ON r.id_pizza = p.id_pizza

            JOIN Vente v
                ON p.id_pizza = v.id_pizza

            GROUP BY i.id_ingredient

            ORDER BY total DESC

            LIMIT 1
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getString("nom_ingredient")
                        + " ("
                        + rs.getInt("total")
                        + " commandes)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Aucun";
    }

    public int nombreTotalVentes() {

        String sql = """
            SELECT COUNT(*) AS total
            FROM Vente
        """;

        try (
            Connection conn = DatabaseConnexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}