package mg.itu.service.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import mg.itu.dto.courant.ActionRole;
import mg.itu.utils.ConfigProperties;
import mg.itu.utils.GsonFactory;
import mg.itu.utils.HttpClient;

/**
 * Service REST pour gérer les virements avec le service Courant
 */
public class VirementService {
    private static final Logger logger = Logger.getLogger(VirementService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;
    
    public VirementService() {
        String baseUrl = ConfigProperties.getProperty("service.courant.baseurl");
        this.httpClient = new HttpClient(baseUrl);
        this.gson = GsonFactory.createGson();
    }
    
    /**
     * Récupérer tous les virements via REST
     */
    public JsonElement getAll() throws IOException {
        try {
            logger.info("Récupération de tous les virements via REST");
            return httpClient.get("/virements");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des virements via REST", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les virements par statut via REST
     */
    public JsonElement getByStatut(Integer statutCode) throws IOException {
        try {
            logger.info("Récupération des virements par statut via REST: " + statutCode);
            return httpClient.get("/virements/statut?code=" + statutCode);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des virements par statut via REST", e);
            throw e;
        }
    }
    
    /**
     * Valider un virement via REST
     */
    public JsonElement valider(Integer virementId,
                              Integer utilisateurId,
                              LocalDateTime dateValidation,
                              List<ActionRole> actionRoles) throws IOException {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("utilisateurId", utilisateurId);
            if (dateValidation != null) {
                payload.put("dateValidation", dateValidation.toString());
            }
            payload.put("actionRoles", actionRoles);
            
            String jsonBody = gson.toJson(payload);
            logger.info("Validation du virement via REST: " + virementId);
            return httpClient.post("/virements/" + virementId + "/valider", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation du virement via REST", e);
            throw e;
        }
    }
    
    /**
     * Refuser un virement via REST
     */
    public JsonElement refuser(Integer virementId,
                              Integer utilisateurId,
                              LocalDateTime dateValidation,
                              List<ActionRole> actionRoles) throws IOException {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("utilisateurId", utilisateurId);
            if (dateValidation != null) {
                payload.put("dateValidation", dateValidation.toString());
            }
            payload.put("actionRoles", actionRoles);
            
            String jsonBody = gson.toJson(payload);
            logger.info("Refus du virement via REST: " + virementId);
            return httpClient.post("/virements/" + virementId + "/refuser", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du refus du virement via REST", e);
            throw e;
        }
    }
}
