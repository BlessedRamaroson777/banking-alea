package mg.itu.database.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    private static Properties dbProperties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        dbProperties = new Properties();
        try (InputStream input = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Impossible de trouver db.properties");
                throw new RuntimeException("Fichier db.properties non trouvé");
            }
            dbProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de db.properties", e);
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            String url = dbProperties.getProperty("db.url");
            String username = dbProperties.getProperty("db.username");
            String password = dbProperties.getProperty("db.password");

            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'obtention d'une connexion à la base de données", e);
        }
    }

    private DB() { }
}
