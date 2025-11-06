package mg.itu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigProperties {
    private static final Logger logger = Logger.getLogger(ConfigProperties.class.getName());
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "application.properties";
    
    static {
        try (InputStream input = ConfigProperties.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warning("Fichier de configuration " + CONFIG_FILE + " non trouvé");
            } else {
                properties.load(input);
                logger.info("Configuration chargée depuis " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.severe("Erreur lors du chargement de la configuration: " + e.getMessage());
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warning("Erreur lors de la conversion de la propriété " + key);
            return defaultValue;
        }
    }
}
