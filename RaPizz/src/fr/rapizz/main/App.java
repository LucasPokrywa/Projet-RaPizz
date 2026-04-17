package fr.rapizz.main;

import java.sql.Connection;
import fr.rapizz.dao.DatabaseConnexion;

public class App {
	public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnexion.getConnection();
            if (conn != null) {
                System.out.println("Succès ! Connecté à la base RaPizz.");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
