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
 * Service pour gérer l'authentification avec les services Change et Courant
 */
public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final HttpClient httpClientChange;
    private final HttpClient httpClientCourant;
    private final Gson gson;
    
    public AuthService() {
        String baseUrlChange = ConfigProperties.getProperty("service.change.baseurl");
        String baseUrlCourant = ConfigProperties.getProperty("service.courant.baseurl");
        this.httpClientChange = new HttpClient(baseUrlChange);
        this.httpClientCourant = new HttpClient(baseUrlCourant);
        this.gson = new Gson();
    }
    
    /**
     * Se connecter: authentifie d'abord sur Courant puis sur Change
     * Retourne un objet combinant les infos de l'utilisateur et de la session Change
     */
    public Map<String, Object> login(String nom, String motDePasse) throws IOException {
        try {
            // 1. Authentifier sur Courant pour obtenir les infos utilisateur
            Map<String, String> courantCredentials = new HashMap<>();
            courantCredentials.put("nom", nom);
            courantCredentials.put("motDePasse", motDePasse);
            
            String courantJsonBody = gson.toJson(courantCredentials);
            logger.info("Authentification de l'utilisateur sur Courant: " + nom);
            JsonElement courantResponse = httpClientCourant.post("/utilisateurs/authenticate", courantJsonBody);
            
            // 2. Extraire les données utilisateur de la réponse Courant
            if (courantResponse == null || !courantResponse.isJsonObject()) {
                throw new IllegalArgumentException("Authentification échouée sur Courant");
            }
            
            int userId = courantResponse.getAsJsonObject().get("id").getAsInt();
            int roleLevel = courantResponse.getAsJsonObject().get("niveau").getAsInt();
            
            // Vérifier que l'ID est valide (différent de 0)
            if (userId == 0) {
                throw new IllegalArgumentException("Authentification échouée: nom d'utilisateur ou mot de passe incorrect");
            }
            
            // 3. Se connecter sur Change avec userId et roleLevel
            Map<String, Integer> changeCredentials = new HashMap<>();
            changeCredentials.put("userId", userId);
            changeCredentials.put("roleLevel", roleLevel);
            
            String changeJsonBody = gson.toJson(changeCredentials);
            logger.info("Connexion de l'utilisateur sur Change: userId=" + userId + ", roleLevel=" + roleLevel);
            JsonElement changeResponse = httpClientChange.post("/auth/login", changeJsonBody);
            
            // 4. Retourner un objet combiné
            Map<String, Object> combinedResponse = new HashMap<>();
            combinedResponse.put("utilisateur", courantResponse);
            combinedResponse.put("sessionChange", changeResponse);
            
            return combinedResponse;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la connexion", e);
            throw e;
        }
    }
    
    /**
     * Se déconnecter du service Change et retourne JsonElement
     */
    public JsonElement logout() throws IOException {
        try {
            logger.info("Déconnexion");
            return httpClientChange.post("/auth/logout", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la déconnexion", e);
            throw e;
        }
    }
    
    /**
     * Récupérer la session courante et retourne JsonElement
     */
    public JsonElement getSession() throws IOException {
        try {
            logger.info("Récupération de la session");
            return httpClientChange.get("/auth/session");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la session", e);
            throw e;
        }
    }
    
    /**
     * Vérifier une permission et retourne JsonElement
     */
    public JsonElement checkPermission(String table, String action) throws IOException {
        try {
            logger.info("Vérification de la permission: " + table + "." + action);
            return httpClientChange.get("/auth/permissions/" + table + "/" + action);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de la permission", e);
            throw e;
        }
    }
    
    /**
     * Recharger les permissions et retourne JsonElement
     */
    public JsonElement refreshPermissions() throws IOException {
        try {
            logger.info("Rechargement des permissions");
            return httpClientChange.post("/auth/refresh-permissions", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du rechargement des permissions", e);
            throw e;
        }
    }
}
