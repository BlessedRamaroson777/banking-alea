package mg.itu.service;

import mg.itu.model.ActionRole;
import mg.itu.security.UserContext;
import mg.itu.security.PermissionManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service d'authentification centralisé
 * Gère la connexion, déconnexion et chargement des permissions
 */
public class AuthService {
    
    private final ActionRoleService actionRoleService;
    
    public AuthService() {
        this.actionRoleService = new ActionRoleService();
    }
    
    /**
     * Connecter un utilisateur et charger ses permissions
     * 
     * @param userId ID de l'utilisateur
     * @param roleLevel Niveau de rôle (1=READ, 2=CREATE/UPDATE, 3=DELETE/VALIDATE)
     * @return Map avec les informations de connexion
     * @throws IllegalArgumentException si les paramètres sont invalides
     * @throws SQLException si erreur lors du chargement des permissions
     */
    public Map<String, Object> login(int userId, int roleLevel) throws IllegalArgumentException, SQLException {
        // Validation des paramètres
        if (userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être positif");
        }
        
        if (roleLevel < 1 || roleLevel > 3) {
            throw new IllegalArgumentException("Le niveau de rôle doit être entre 1 et 3");
        }
        
        // Vider le cache des permissions avant de charger les nouvelles
        PermissionManager.clearCache();
        
        // Charger toutes les permissions accessibles pour ce niveau de rôle dans le cache
        loadPermissionsForRole(roleLevel);
        
        // Définir le contexte utilisateur
        UserContext.login(userId, roleLevel);
        
        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Connexion réussie");
        response.put("userId", userId);
        response.put("roleLevel", roleLevel);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * Déconnecter l'utilisateur et vider le cache des permissions
     * 
     * @return Map avec les informations de déconnexion
     * @throws IllegalStateException si aucun utilisateur n'est connecté
     */
    public Map<String, Object> logout() throws IllegalStateException {
        if (!UserContext.isUserLoggedIn()) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        
        int userId = UserContext.getUserId();
        
        // Vider le cache des permissions
        PermissionManager.clearCache();
        
        // Déconnecter l'utilisateur
        UserContext.logout();
        
        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Déconnexion réussie");
        response.put("userId", userId);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * Obtenir les informations de la session courante
     * 
     * @return Map avec les informations de session
     */
    public Map<String, Object> getSession() {
        Map<String, Object> response = new HashMap<>();
        
        if (UserContext.isUserLoggedIn()) {
            response.put("loggedIn", true);
            response.put("userId", UserContext.getUserId());
            response.put("roleLevel", UserContext.getUserRoleLevel());
        } else {
            response.put("loggedIn", false);
            response.put("message", "Aucun utilisateur connecté");
        }
        
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * Vérifier si l'utilisateur connecté a une permission spécifique
     * 
     * @param tableName Nom de la table
     * @param action Action à vérifier
     * @return Map avec les informations de permission
     */
    public Map<String, Object> checkPermission(String tableName, String action) {
        Map<String, Object> response = new HashMap<>();
        
        if (!UserContext.isUserLoggedIn()) {
            response.put("hasPermission", false);
            response.put("message", "Aucun utilisateur connecté");
            response.put("table", tableName);
            response.put("action", action);
            response.put("timestamp", System.currentTimeMillis());
            return response;
        }
        
        boolean hasPermission = PermissionManager.hasPermission(tableName, action);
        int requiredLevel = PermissionManager.getRequiredRoleLevel(tableName, action);
        int userLevel = UserContext.getUserRoleLevel();
        
        response.put("hasPermission", hasPermission);
        response.put("table", tableName);
        response.put("action", action);
        response.put("userRoleLevel", userLevel);
        response.put("requiredRoleLevel", requiredLevel);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * Vider le cache des permissions et recharger
     * 
     * @return Map avec le résultat de l'opération
     */
    public Map<String, Object> refreshPermissions() throws SQLException {
        PermissionManager.clearCache();
        
        // Si un utilisateur est connecté, recharger ses permissions
        if (UserContext.isUserLoggedIn()) {
            int roleLevel = UserContext.getUserRoleLevel();
            loadPermissionsForRole(roleLevel);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cache des permissions rechargé avec succès");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    /**
     * Vérifier si un utilisateur est actuellement connecté
     */
    public boolean isUserLoggedIn() {
        return UserContext.isUserLoggedIn();
    }
    
    /**
     * Obtenir l'ID de l'utilisateur connecté
     */
    public int getCurrentUserId() {
        return UserContext.isUserLoggedIn() ? UserContext.getUserId() : -1;
    }
    
    /**
     * Obtenir le niveau de rôle de l'utilisateur connecté
     */
    public int getCurrentUserRoleLevel() {
        return UserContext.isUserLoggedIn() ? UserContext.getUserRoleLevel() : -1;
    }
    
    /**
     * Charger uniquement les permissions accessibles pour le niveau de rôle de l'utilisateur
     * Ne charge que les permissions où role_minimum <= roleLevel de l'utilisateur
     * 
     * @param roleLevel Niveau de rôle de l'utilisateur
     * @throws SQLException si erreur lors du chargement
     */
    private void loadPermissionsForRole(int roleLevel) throws SQLException {
        // Récupérer uniquement les permissions accessibles pour ce niveau de rôle
        // Utiliser le filtre avec l'opérateur <= pour récupérer role_minimum <= roleLevel
        Map<String, Object> filters = new HashMap<>();
        filters.put("roleMinimum <=", roleLevel);
        
        List<ActionRole> accessiblePermissions = actionRoleService.getAll(filters, null, null, null);
        
        // Charger les permissions accessibles dans le cache
        for (ActionRole permission : accessiblePermissions) {
            PermissionManager.preloadPermission(
                permission.getNomTable(), 
                permission.getNomAction(), 
                permission.getRoleMinimum()
            );
        }
    }
}
