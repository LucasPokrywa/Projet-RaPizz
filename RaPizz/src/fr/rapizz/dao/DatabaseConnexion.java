package fr.rapizz.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/RaPizz";
    private static final String USER = "rapizz"; 
    private static final String PASSWORD = "rapizz";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}