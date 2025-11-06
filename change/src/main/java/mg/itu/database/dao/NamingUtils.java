package mg.itu.database.dao;

/**
 * Utilitaire pour la conversion des noms entre camelCase et snake_case
 */
public class NamingUtils {
    
    /**
     * Convertit un nom camelCase en snake_case
     * Exemple: firstName -> first_name
     */
    public static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Convertit un nom snake_case en camelCase
     * Exemple: first_name -> firstName
     */
    public static String snakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < snakeCase.length(); i++) {
            char ch = snakeCase.charAt(i);
            if (ch == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(ch));
                    capitalizeNext = false;
                } else {
                    result.append(ch);
                }
            }
        }
        
        return result.toString();
    }
}
