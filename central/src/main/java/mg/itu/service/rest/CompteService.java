package mg.itu.service.rest;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Service REST pour gérer les comptes avec le service Courant
 */
public class CompteService {
    private static final Logger logger = Logger.getLogger(CompteService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;
    
    public CompteService() {
        String baseUrl = ConfigProperties.getProperty("service.courant.baseurl");
        this.httpClient = new HttpClient(baseUrl);
        this.gson = GsonFactory.createGson();
    }
    
    /**
     * Récupérer tous les comptes via REST
     */
    public JsonElement getAll() throws IOException {
        try {
            logger.info("Récupération de tous les comptes via REST");
            return httpClient.get("/comptes");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des comptes via REST", e);
            throw e;
        }
    }
    
    /**
     * Effectuer un virement entre deux comptes via REST
     */
    public JsonElement virer(Integer compteEnvoyeurId,
                            Integer compteDestinataireId,
                            BigDecimal montant,
                            LocalDateTime dateCreation,
                            LocalDateTime dateEffet,
                            Integer utilisateurId,
                            Integer changeId,
                            List<ActionRole> actionRoles) throws IOException {
        try {
            // Si dateCreation est null, utiliser la date actuelle
            if (dateCreation == null) {
                dateCreation = LocalDateTime.now();
            }
            
            // Si dateEffet est null, utiliser dateCreation
            if (dateEffet == null) {
                dateEffet = dateCreation;
            }
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("compteEnvoyeurId", compteEnvoyeurId);
            payload.put("compteDestinataireId", compteDestinataireId);
            payload.put("montant", montant);
            payload.put("dateCreation", dateCreation.toString());
            payload.put("dateEffet", dateEffet.toString());
            payload.put("utilisateurId", utilisateurId);
            payload.put("changeId", changeId);
            payload.put("actionRoles", actionRoles);
            
            String jsonBody = gson.toJson(payload);
            logger.info("Virement via REST: " + montant + " du compte " + compteEnvoyeurId + " vers " + compteDestinataireId);
            return httpClient.post("/comptes/virements", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du virement via REST", e);
            throw e;
        }
    }
}
