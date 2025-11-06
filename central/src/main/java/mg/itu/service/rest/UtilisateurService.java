package mg.itu.service.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import mg.itu.utils.ConfigProperties;
import mg.itu.utils.HttpClient;

/**
 * Service REST pour gérer les utilisateurs avec le service Courant
 */
public class UtilisateurService {
    private static final Logger logger = Logger.getLogger(UtilisateurService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;
    
    public UtilisateurService() {
        String baseUrl = ConfigProperties.getProperty("service.courant.baseurl");
        this.httpClient = new HttpClient(baseUrl);
        this.gson = new Gson();
    }
    
    /**
     * Authentifier un utilisateur via REST
     */
    public JsonElement authenticate(String nom, String motDePasse) throws IOException {
        try {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("nom", nom);
            credentials.put("motDePasse", motDePasse);
            
            String jsonBody = gson.toJson(credentials);
            logger.info("Authentification de l'utilisateur via REST: " + nom);
            return httpClient.post("/utilisateurs/authenticate", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'authentification via REST", e);
            throw e;
        }
    }
}
