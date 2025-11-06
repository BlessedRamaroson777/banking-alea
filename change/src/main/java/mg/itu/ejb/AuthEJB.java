package mg.itu.ejb;

import mg.itu.ejb.remote.AuthRemote;
import mg.itu.service.AuthService;
import jakarta.ejb.Stateless;
import java.sql.SQLException;
import java.util.Map;

/**
 * EJB Stateless pour l'authentification et la gestion de session
 * Permet l'utilisation du module d'authentification via EJB
 */
@Stateless
public class AuthEJB implements AuthRemote {
    
    private final AuthService authService = new AuthService();
    
    @Override
    public Map<String, Object> login(int userId, int roleLevel) throws IllegalArgumentException {
        try {
            return authService.login(userId, roleLevel);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Erreur lors du chargement des permissions: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> logout() throws IllegalStateException {
        return authService.logout();
    }
    
    @Override
    public Map<String, Object> getSession() {
        return authService.getSession();
    }
    
    @Override
    public Map<String, Object> checkPermission(String tableName, String action) {
        return authService.checkPermission(tableName, action);
    }
    
    @Override
    public Map<String, Object> clearPermissionCache() {
        try {
            return authService.refreshPermissions();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du rechargement des permissions: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isUserLoggedIn() {
        return authService.isUserLoggedIn();
    }
    
    @Override
    public int getCurrentUserId() {
        return authService.getCurrentUserId();
    }
    
    @Override
    public int getCurrentUserRoleLevel() {
        return authService.getCurrentUserRoleLevel();
    }
}
