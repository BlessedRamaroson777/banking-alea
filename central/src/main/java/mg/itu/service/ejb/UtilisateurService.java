package mg.itu.service.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.dto.courant.Utilisateur;
import mg.itu.services.RemoteUtilisateurService;
import mg.itu.utils.LookupJNDIHelper;

/**
 * Service EJB pour gérer les utilisateurs avec le service Courant via EJB Local
 */
public class UtilisateurService {
    private static final Logger logger = Logger.getLogger(UtilisateurService.class.getName());
    
    /**
     * Convertir un model Utilisateur du serveur courant vers un DTO
     */
    private Utilisateur toDTO(mg.itu.models.Utilisateur model) {
        if (model == null) return null;
        
        Utilisateur dto = new Utilisateur();
        dto.setId(model.getId());
        dto.setNom(model.getNom());
        dto.setMotDePasse(model.getMotDePasse());
        dto.setNiveau(model.getNiveau());
        return dto;
    }
    
    /**
     * Authentifier un utilisateur via EJB
     */
    public Utilisateur authenticate(String nom, String motDePasse) throws Exception {
        try {
            RemoteUtilisateurService utilisateurService = LookupJNDIHelper.getUtilisateurService();
            logger.info("Authentification de l'utilisateur via EJB: " + nom);
            
            mg.itu.models.Utilisateur result = utilisateurService.authenticate(nom, motDePasse);
            return toDTO(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de l'authentification via EJB", e);
            throw e;
        }
    }
}
