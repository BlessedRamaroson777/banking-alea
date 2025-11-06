package mg.itu.security;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de vérification des permissions
 * Les permissions sont chargées dans le cache lors du login
 * et vérifiées uniquement depuis le cache (pas de requête DB pendant l'utilisation)
 */
public class PermissionManager {
    
    // Cache des permissions chargées lors du login
    private static final Map<String, Integer> permissionCache = new HashMap<>();
    
    /**
     * Vérifier si l'utilisateur connecté a la permission d'effectuer une action
     * Vérifie uniquement dans le cache (les permissions doivent être chargées au login)
     * 
     * @param tableName Nom de la table (ex: "devises")
     * @param action Action à effectuer (ex: "CREATE", "READ", "UPDATE", "DELETE", "VALIDATE")
     * @throws SecurityException si l'utilisateur n'a pas la permission
     */
    public static void checkPermission(String tableName, String action) {
        // Si aucun utilisateur n'est connecté
        if (!UserContext.isUserLoggedIn()) {
            throw new SecurityException("Aucun utilisateur connecté. Veuillez vous authentifier.");
        }
        
        // Récupérer le niveau de rôle de l'utilisateur connecté
        int userRoleLevel = UserContext.getUserRoleLevel();
        
        // Créer la clé de cache
        String cacheKey = tableName.toLowerCase() + ":" + action.toUpperCase();
        
        // Vérifier si la permission existe dans le cache
        if (!permissionCache.containsKey(cacheKey)) {
            throw new SecurityException(
                String.format("Permission non définie pour l'action '%s' sur '%s'", action, tableName)
            );
        }
        
        // Récupérer le niveau minimum requis depuis le cache
        int requiredRoleLevel = permissionCache.get(cacheKey);
        
        // Vérifier si l'utilisateur a le niveau requis
        if (userRoleLevel < requiredRoleLevel) {
            throw new SecurityException(
                String.format("Permission refusée. Action '%s' sur '%s' nécessite un niveau de rôle %d (vous avez: %d)",
                    action, tableName, requiredRoleLevel, userRoleLevel)
            );
        }
    }
    
    /**
     * Pré-charger une permission dans le cache
     * Utilisé lors du login pour charger toutes les permissions
     * 
     * @param tableName Nom de la table
     * @param action Action
     * @param roleMinimum Niveau de rôle minimum requis
     */
    public static void preloadPermission(String tableName, String action, int roleMinimum) {
        String cacheKey = tableName.toLowerCase() + ":" + action.toUpperCase();
        permissionCache.put(cacheKey, roleMinimum);
    }
    
    /**
     * Vider le cache des permissions
     * Appelé lors du logout
     */
    public static void clearCache() {
        permissionCache.clear();
    }
    
    /**
     * Vérifier si l'utilisateur a la permission sans lever d'exception
     * 
     * @return true si l'utilisateur a la permission, false sinon
     */
    public static boolean hasPermission(String tableName, String action) {
        try {
            checkPermission(tableName, action);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtenir le niveau de rôle requis pour une action depuis le cache
     * 
     * @return Niveau de rôle requis ou -1 si non trouvé
     */
    public static int getRequiredRoleLevel(String tableName, String action) {
        String cacheKey = tableName.toLowerCase() + ":" + action.toUpperCase();
        return permissionCache.getOrDefault(cacheKey, -1);
    }
}
