package mg.itu.service.rest;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import mg.itu.utils.ConfigProperties;
import mg.itu.utils.HttpClient;

/**
 * Service pour gérer les ActionRole avec l'API Courant
 */
public class ActionRoleService {
    private static final Logger logger = Logger.getLogger(ActionRoleService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;
    
    public ActionRoleService() {
        String baseUrl = ConfigProperties.getProperty("service.courant.baseurl");
        this.httpClient = new HttpClient(baseUrl);
        this.gson = new Gson();
    }
    
    /**
     * Récupérer tous les ActionRole
     */
    public JsonElement getAll() throws IOException {
        try {
            logger.info("Récupération de tous les ActionRole");
            return httpClient.get("/actionroles");
        } catch (IOException e) {
            logger.severe("Erreur lors de la récupération des ActionRole: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Récupérer les ActionRole accessibles pour un niveau d'utilisateur donné
     * (roleMinimum <= niveau)
     */
    public JsonElement getByNiveau(Integer niveau) throws IOException {
        try {
            logger.info("Récupération des ActionRole pour niveau: " + niveau);
            return httpClient.get("/actionroles/niveau/" + niveau);
        } catch (IOException e) {
            logger.severe("Erreur lors de la récupération des ActionRole par niveau: " + e.getMessage());
            throw e;
        }
    }
}
