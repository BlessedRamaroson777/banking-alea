package mg.itu.service.rest;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import mg.itu.dto.change.Devise;
import mg.itu.utils.ConfigProperties;
import mg.itu.utils.GsonFactory;
import mg.itu.utils.HttpClient;

/**
 * Service pour gérer les appels au service Change - Devises
 */
public class DeviseService {
    private static final Logger logger = Logger.getLogger(DeviseService.class.getName());
    private final HttpClient httpClient;
    private final Gson gson;
    
    public DeviseService() {
        String baseUrl = ConfigProperties.getProperty("service.change.baseurl");
        this.httpClient = new HttpClient(baseUrl);
        this.gson = GsonFactory.createGson();
    }
    
    /**
     * Créer une nouvelle devise
     */
    public JsonElement create(Devise devise) throws IOException {
        try {
            String jsonBody = gson.toJson(devise);
            logger.info("Création d'une nouvelle devise");
            return httpClient.post("/devises", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la devise", e);
            throw e;
        }
    }
    
    /**
     * Récupérer une devise par ID
     */
    public JsonElement getById(int id) throws IOException {
        try {
            logger.info("Récupération de la devise: " + id);
            return httpClient.get("/devises/" + id);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la devise: " + id, e);
            throw e;
        }
    }
    
    /**
     * Récupérer toutes les devises
     */
    public JsonElement getAll() throws IOException {
        try {
            logger.info("Récupération de toutes les devises");
            return httpClient.get("/devises");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de toutes les devises", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises avec filtres
     */
    public JsonElement getAllWithFilters(Map<String, String> filters) throws IOException {
        try {
            StringBuilder queryParams = new StringBuilder();
            
            if (filters != null && !filters.isEmpty()) {
                queryParams.append("?");
                filters.forEach((key, value) ->
                    queryParams.append(key).append("=").append(value).append("&"));
                // Supprimer le dernier &
                if (queryParams.length() > 1) {
                    queryParams.setLength(queryParams.length() - 1);
                }
            }
            
            logger.info("Récupération des devises avec filtres");
            return httpClient.get("/devises" + queryParams);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises avec filtres", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises actuellement actives
     */
    public JsonElement getActiveDevises() throws IOException {
        try {
            logger.info("Récupération des devises actives");
            return httpClient.get("/devises/active");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises actives", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises actives à une date donnée
     */
    public JsonElement getActiveDevises(java.time.LocalDate date) throws IOException {
        try {
            logger.info("Récupération des devises actives à la date: " + date);
            return httpClient.get("/devises/active?date=" + date.toString());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises actives à la date: " + date, e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises actuellement actives (alias)
     */
    public JsonElement getCurrentlyActiveDevises() throws IOException {
        return getActiveDevises();
    }
    
    /**
     * Récupérer les devises en attente de validation
     */
    public JsonElement getDevisesEnAttente() throws IOException {
        try {
            logger.info("Récupération des devises en attente");
            return httpClient.get("/devises/en-attente");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises en attente", e);
            throw e;
        }
    }
    
    /**
     * Récupérer une devise par code
     */
    public JsonElement getByCode(String code) throws IOException {
        try {
            logger.info("Récupération de la devise avec code: " + code);
            return httpClient.get("/devises/code/" + code);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la devise avec code: " + code, e);
            throw e;
        }
    }
    
    /**
     * Valider une devise
     */
    public JsonElement valider(int id, java.time.LocalDate dateValidation) throws IOException {
        try {
            logger.info("Validation de la devise: " + id + " avec date: " + dateValidation);
            
            // Créer le body JSON avec la date de validation
            String jsonBody = null;
            if (dateValidation != null) {
                jsonBody = "{\"dateValidation\":\"" + dateValidation.toString() + "\"}";
            }
            
            return httpClient.post("/devises/" + id + "/valider", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation de la devise: " + id, e);
            throw e;
        }
    }
    
    /**
     * Refuser une devise
     */
    public JsonElement refuser(int id) throws IOException {
        try {
            logger.info("Refus de la devise: " + id);
            return httpClient.post("/devises/" + id + "/refuser", null);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du refus de la devise: " + id, e);
            throw e;
        }
    }
    
    /**
     * Proposer une modification de devise
     */
    public JsonElement proposeModification(int id, Map<String, Object> modificationData) throws IOException {
        try {
            String jsonBody = gson.toJson(modificationData);
            logger.info("Proposition de modification pour la devise: " + id);
            return httpClient.post("/devises/" + id + "/propose-modification", jsonBody);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la proposition de modification de la devise: " + id, e);
            throw e;
        }
    }
}
