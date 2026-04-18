package fr.rapizz.dao;

import fr.rapizz.model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    
	public boolean creerClient(Client client) {
	    String sql = "INSERT INTO Client (nom_client, prenom_client, adresse, telephone) VALUES (?, ?, ?, ?, ?, ?)";
	    try (Connection conn = DatabaseConnexion.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, client.getNomClient());
	        pstmt.setString(2, client.getPrenomClient());
	        pstmt.setString(3, client.getAdresse());
	        pstmt.setString(4, client.getTelephone());
	        
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
    public double obtenirSolde(int idClient) {
        String sql = "SELECT solde_compte FROM Client WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("solde_compte");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public int recupererNbPizzaCommandees(int idClient) {
        String sql = "SELECT pizza_commande FROM Client WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("pizza_commande");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean ajouterAuSolde(int idClient, double montant) {
        String sql = "UPDATE Client SET solde_compte = solde_compte + ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean retirerDuSolde(int idClient, double montant) {
        if (obtenirSolde(idClient) < montant) return false;

        String sql = "UPDATE Client SET solde_compte = solde_compte - ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean ajouterPizzaCommander(int idClient, int nombre) {
        String sql = "UPDATE Client SET pizza_commande = pizza_commande + ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nombre);
            pstmt.setInt(2, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean augmenterNbPizza(int idClient) {
        String sql = "UPDATE Client SET pizza_commande = pizza_commande + 1 WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    // Changement info clients
    
    public boolean changerNomPrenom(int idClient, String nouveauNom, String nouveauPrenom) {
        String sql = "UPDATE Client SET nom_client = ?, prenom_client = ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauNom);
            pstmt.setString(2, nouveauPrenom);
            pstmt.setInt(3, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
      
    public boolean changerAdresse(int idClient, String nouvelleAdresse) {
        String sql = "UPDATE Client SET adresse = ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouvelleAdresse);
            pstmt.setInt(2, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean changerTelephone(int idClient, String nouveauTel) {
        String sql = "UPDATE Client SET telephone = ? WHERE id_client = ?";
        try (Connection conn = DatabaseConnexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauTel);
            pstmt.setInt(2, idClient);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public Client meilleurClient() {
    	Client client = null;
        String sql = "SELECT * FROM Client ORDER BY pizza_commande DESC LIMIT 1";

        try (Connection conn = DatabaseConnexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNomClient(rs.getString("nom_client"));
                client.setPrenomClient(rs.getString("prenom_client"));
                client.setAdresse(rs.getString("adresse"));
                client.setTelephone(rs.getString("telephone"));
                client.setSoldeCompte(rs.getDouble("solde_compte"));
                client.setPizzaCommande(rs.getInt("pizza_commande"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return client;
    }
}