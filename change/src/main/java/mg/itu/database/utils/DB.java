package mg.itu.database.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    private static DB instance;
    private static Connection connection;
    private static Properties dbProperties;

    static {
        loadProperties();
        DB.openConnection();
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

    public static void openConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = dbProperties.getProperty("db.url");
            String username = dbProperties.getProperty("db.username");
            String password = dbProperties.getProperty("db.password");
            
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion à la base de données établie avec succès.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    private DB() {
        // Constructeur privé pour le singleton
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
