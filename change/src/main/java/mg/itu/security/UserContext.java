package mg.itu.security;

/**
 * Contexte utilisateur pour gérer la session et les permissions
 * Utilise ThreadLocal pour isoler les sessions par thread
 */
public class UserContext {
    
    // ThreadLocal pour stocker le niveau de rôle de l'utilisateur par thread
    private static final ThreadLocal<Integer> userRoleLevel = new ThreadLocal<>();
    
    // ThreadLocal pour stocker l'ID de l'utilisateur
    private static final ThreadLocal<Integer> userId = new ThreadLocal<>();
    
    /**
     * Définir le niveau de rôle de l'utilisateur connecté
     * Appelé lors du login
     */
    public static void setUserRoleLevel(int roleLevel) {
        userRoleLevel.set(roleLevel);
    }
    
    /**
     * Récupérer le niveau de rôle de l'utilisateur connecté
     * Retourne 0 si aucun utilisateur n'est connecté
     */
    public static int getUserRoleLevel() {
        Integer level = userRoleLevel.get();
        return level != null ? level : 0;
    }
    
    /**
     * Définir l'ID de l'utilisateur connecté
     */
    public static void setUserId(int id) {
        userId.set(id);
    }
    
    /**
     * Récupérer l'ID de l'utilisateur connecté
     * Retourne 0 si aucun utilisateur n'est connecté
     */
    public static int getUserId() {
        Integer id = userId.get();
        return id != null ? id : 0;
    }
    
    /**
     * Vérifier si un utilisateur est connecté
     */
    public static boolean isUserLoggedIn() {
        return userRoleLevel.get() != null && userId.get() != null;
    }
    
    /**
     * Libérer le contexte utilisateur
     * Appelé lors du logout
     */
    public static void clear() {
        userRoleLevel.remove();
        userId.remove();
    }
    
    /**
     * Simuler un login (pour tests)
     */
    public static void login(int userId, int roleLevel) {
        setUserId(userId);
        setUserRoleLevel(roleLevel);
    }
    
    /**
     * Logout
     */
    public static void logout() {
        clear();
    }
}
