package mg.itu.service.ejb;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.dto.courant.Utilisateur;
import mg.itu.ejb.remote.AuthRemote;
import mg.itu.services.RemoteUtilisateurService;
import mg.itu.utils.LookupJNDIHelper;

/**
 * Service EJB pour gérer l'authentification avec les services Change et Courant via EJB
 */
public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    
    /**
     * Se connecter: authentifie d'abord sur Courant via EJB puis sur Change via EJB
     * Retourne un objet combinant les infos de l'utilisateur et de la session Change
     */
    public Map<String, Object> login(String nom, String motDePasse) throws Exception {
        try {
            // 1. Authentifier sur Courant pour obtenir les infos utilisateur
            RemoteUtilisateurService utilisateurService = LookupJNDIHelper.getUtilisateurService();
            logger.info("Authentification de l'utilisateur sur Courant via EJB: " + nom);
            mg.itu.models.Utilisateur utilisateurModel = utilisateurService.authenticate(nom, motDePasse);
            
            // 2. Vérifier que l'authentification a réussi
            if (utilisateurModel == null || utilisateurModel.getId() == null || utilisateurModel.getId() == 0) {
                throw new IllegalArgumentException("Authentification échouée: nom d'utilisateur ou mot de passe incorrect");
            }
            
            int userId = utilisateurModel.getId();
            int roleLevel = utilisateurModel.getNiveau();
            
            // 3. Se connecter sur Change avec userId et roleLevel
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            logger.info("Connexion de l'utilisateur sur Change via EJB: userId=" + userId + ", roleLevel=" + roleLevel);
            Map<String, Object> changeSession = authRemote.login(userId, roleLevel);
            
            // 4. Convertir l'utilisateur en DTO
            Utilisateur utilisateurDTO = new Utilisateur();
            utilisateurDTO.setId(utilisateurModel.getId());
            utilisateurDTO.setNom(utilisateurModel.getNom());
            utilisateurDTO.setMotDePasse(utilisateurModel.getMotDePasse());
            utilisateurDTO.setNiveau(utilisateurModel.getNiveau());
            
            // 5. Retourner un objet combiné
            Map<String, Object> combinedResponse = new HashMap<>();
            combinedResponse.put("utilisateur", utilisateurDTO);
            combinedResponse.put("sessionChange", changeSession);
            
            return combinedResponse;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la connexion via EJB", e);
            throw e;
        }
    }
    
    /**
     * Se déconnecter du service Change via EJB
     */
    public Map<String, Object> logout() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            logger.info("Déconnexion via EJB");
            return authRemote.logout();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la déconnexion via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer la session courante via EJB
     */
    public Map<String, Object> getSession() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            logger.info("Récupération de la session via EJB");
            return authRemote.getSession();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la session via EJB", e);
            throw e;
        }
    }
    
    /**
     * Vérifier une permission via EJB
     */
    public Map<String, Object> checkPermission(String tableName, String action) throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            logger.info("Vérification de la permission via EJB: " + tableName + "." + action);
            return authRemote.checkPermission(tableName, action);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de la permission via EJB", e);
            throw e;
        }
    }
    
    /**
     * Vider le cache des permissions via EJB
     */
    public Map<String, Object> clearPermissionCache() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            logger.info("Vidage du cache des permissions via EJB");
            return authRemote.clearPermissionCache();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du vidage du cache via EJB", e);
            throw e;
        }
    }
    
    /**
     * Vérifier si un utilisateur est connecté via EJB
     */
    public boolean isUserLoggedIn() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            return authRemote.isUserLoggedIn();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de connexion via EJB", e);
            throw e;
        }
    }
    
    /**
     * Obtenir l'ID de l'utilisateur connecté via EJB
     */
    public int getCurrentUserId() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            return authRemote.getCurrentUserId();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de l'ID utilisateur via EJB", e);
            throw e;
        }
    }
    
    /**
     * Obtenir le niveau de rôle de l'utilisateur connecté via EJB
     */
    public int getCurrentUserRoleLevel() throws Exception {
        try {
            AuthRemote authRemote = LookupJNDIHelper.getAuthService();
            return authRemote.getCurrentUserRoleLevel();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du niveau de rôle via EJB", e);
            throw e;
        }
    }
}
