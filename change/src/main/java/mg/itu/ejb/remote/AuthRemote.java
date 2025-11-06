package mg.itu.ejb.remote;

import jakarta.ejb.Remote;
import java.util.Map;

/**
 * Interface Remote pour l'authentification et la gestion de session
 * Permet l'utilisation du module via EJB
 */
@Remote
public interface AuthRemote {
    
    /**
     * Connecter un utilisateur avec son ID et son niveau de rôle
     * @param userId ID de l'utilisateur
     * @param roleLevel Niveau de rôle (1=READ, 2=CREATE/UPDATE, 3=DELETE/VALIDATE)
     * @return Map avec les informations de connexion
     * @throws IllegalArgumentException si les paramètres sont invalides
     */
    Map<String, Object> login(int userId, int roleLevel) throws IllegalArgumentException;
    
    /**
     * Déconnecter l'utilisateur actuel
     * @return Map avec les informations de déconnexion
     * @throws IllegalStateException si aucun utilisateur n'est connecté
     */
    Map<String, Object> logout() throws IllegalStateException;
    
    /**
     * Obtenir les informations de la session courante
     * @return Map avec les informations de session (userId, roleLevel, loggedIn)
     */
    Map<String, Object> getSession();
    
    /**
     * Vérifier si l'utilisateur connecté a une permission spécifique
     * @param tableName Nom de la table
     * @param action Action à vérifier (READ, CREATE, UPDATE, DELETE, VALIDATE)
     * @return Map avec les informations de permission
     */
    Map<String, Object> checkPermission(String tableName, String action);
    
    /**
     * Vider le cache des permissions (force une relecture depuis la base de données)
     * @return Map avec le résultat de l'opération
     */
    Map<String, Object> clearPermissionCache();
    
    /**
     * Vérifier si un utilisateur est actuellement connecté
     * @return true si un utilisateur est connecté, false sinon
     */
    boolean isUserLoggedIn();
    
    /**
     * Obtenir l'ID de l'utilisateur connecté
     * @return ID de l'utilisateur ou -1 si aucun utilisateur n'est connecté
     */
    int getCurrentUserId();
    
    /**
     * Obtenir le niveau de rôle de l'utilisateur connecté
     * @return Niveau de rôle ou -1 si aucun utilisateur n'est connecté
     */
    int getCurrentUserRoleLevel();
}
