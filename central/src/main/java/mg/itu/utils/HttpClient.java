package mg.itu.utils;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Classe utilitaire pour les appels HTTP REST
 * Gère GET, POST, PUT, DELETE, PATCH avec logging
 * Retourne des JsonElement
 */
public class HttpClient {
    private static final Logger logger = Logger.getLogger(HttpClient.class.getName());
    private final String baseUrl;
    
    /**
     * Constructeur avec URL de base
     */
    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        logger.info("HttpClient initialisé avec baseUrl: " + baseUrl);
    }
    
    /**
     * Effectue une requête GET et retourne JsonElement
     */
    public JsonElement get(String endpoint) throws IOException {
        String url = baseUrl + endpoint;
        logger.info("GET request: " + url);
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                logger.info("GET response status: " + statusCode);
                logger.info("GET response body: " + responseBody);
                
                // Vérifier le code HTTP
                if (statusCode >= 400) {
                    logger.severe("Erreur HTTP " + statusCode + " pour GET " + url + ": " + responseBody);
                    throw new IOException("Erreur HTTP " + statusCode + ": " + responseBody);
                }
                
                // Vérifier que la réponse n'est pas vide
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    logger.warning("Réponse vide pour GET " + url);
                    return JsonParser.parseString("{}");
                }

                return JsonParser.parseString(responseBody);
            } catch (ParseException e) {
                logger.severe("Erreur lors du parsing de la réponse GET: " + e.getMessage());
                throw new IOException("Erreur de parsing de la réponse", e);
            }
        } catch (IOException e) {
            logger.severe("Erreur lors de l'appel GET " + url + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Effectue une requête POST avec un body JSON et retourne JsonElement
     */
    public JsonElement post(String endpoint, String jsonBody) throws IOException {
        String url = baseUrl + endpoint;
        logger.info("POST request: " + url);
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");
            
            if (jsonBody != null && !jsonBody.isEmpty()) {
                request.setEntity(new StringEntity(jsonBody));
                logger.fine("POST body: " + jsonBody);
            }
            
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                logger.info("POST response status: " + statusCode);
                logger.info("POST response body: " + responseBody);
                
                // Vérifier le code HTTP
                if (statusCode >= 400) {
                    logger.severe("Erreur HTTP " + statusCode + " pour POST " + url + ": " + responseBody);
                    throw new IOException("Erreur HTTP " + statusCode + ": " + responseBody);
                }
                
                // Vérifier que la réponse n'est pas vide
                if (responseBody == null || responseBody.trim().isEmpty()) {
                    logger.warning("Réponse vide pour POST " + url);
                    return JsonParser.parseString("{}");
                }
                
                return JsonParser.parseString(responseBody);
            } catch (ParseException e) {
                logger.severe("Erreur lors du parsing de la réponse POST: " + e.getMessage());
                throw new IOException("Erreur de parsing de la réponse", e);
            }
        } catch (IOException e) {
            logger.severe("Erreur lors de l'appel POST " + url + ": " + e.getMessage());
            throw e;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
