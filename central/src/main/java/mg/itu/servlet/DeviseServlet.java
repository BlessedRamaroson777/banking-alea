package mg.itu.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.dto.change.Devise;
import mg.itu.service.rest.DeviseService;

/**
 * Servlet pour gérer les devises
 */
@WebServlet(urlPatterns = {"/devises", "/devises/*"})
public class DeviseServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(DeviseServlet.class.getName());
    private final DeviseService deviseService = new DeviseService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        // Endpoint API pour les devises actives par date
        if ("/actives".equals(pathInfo)) {
            handleGetActivesDevises(request, response);
            return;
        }
        
        // Gérer les messages de succès
        String successParam = request.getParameter("success");
        String successMessage = null;
        
        if (successParam != null) {
            switch (successParam) {
                case "created":
                    successMessage = "Devise créée avec succès";
                    break;
                case "validated":
                    successMessage = "Devise validée avec succès";
                    break;
                case "refused":
                    successMessage = "Devise refusée avec succès";
                    break;
            }
        }
        
        loadDevises(request, response, null, successMessage);
    }
    
    /**
     * Endpoint API pour récupérer les devises actives à une date donnée
     */
    private void handleGetActivesDevises(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String dateStr = request.getParameter("date");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            JsonElement devises;
            if (dateStr != null && !dateStr.isEmpty()) {
                // Récupérer les devises actives à la date donnée
                java.time.LocalDate date = java.time.LocalDate.parse(dateStr.substring(0, 10));
                devises = deviseService.getActiveDevises(date);
            } else {
                // Récupérer les devises actuellement actives
                devises = deviseService.getCurrentlyActiveDevises();
            }
            
            response.getWriter().write(devises.toString());
        } catch (java.time.format.DateTimeParseException e) {
            logger.log(Level.WARNING, "Format de date invalide: " + dateStr, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Format de date invalide\"}");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises actives", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Erreur serveur\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            handleCreate(request, response);
        } else if (pathInfo.equals("/valider")) {
            handleValider(request, response);
        } else if (pathInfo.equals("/refuser")) {
            handleRefuser(request, response);
        }
    }
    
    /**
     * Gérer la création d'une nouvelle devise
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String code = request.getParameter("code");
        String dateDebut = request.getParameter("dateDebut");
        String dateFin = request.getParameter("dateFin");
        String cours = request.getParameter("cours");
        
        if (code == null || dateDebut == null || cours == null) {
            loadDevises(request, response, "Tous les champs obligatoires sont requis", null);
            return;
        }
        
        try {
            // Créer l'objet Devise
            Devise devise = new Devise();
            devise.setCode(code);
            devise.setDateDebut(java.time.LocalDate.parse(dateDebut));
            
            // Date fin optionnelle
            if (dateFin != null && !dateFin.trim().isEmpty()) {
                devise.setDateFin(java.time.LocalDate.parse(dateFin));
            }
            
            devise.setCours(new java.math.BigDecimal(cours));
            devise.setStatutCode(1); // Statut par défaut = 1
            
            logger.info("Objet Devise créé: " + devise.toString());
            
            // Créer la devise via le service
            JsonElement result = deviseService.create(devise);
            
            logger.info("Réponse de l'API: " + result.toString());
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject()) {
                com.google.gson.JsonObject jsonObj = result.getAsJsonObject();
                
                // Vérifier si l'objet a un champ "error"
                if (jsonObj.has("error")) {
                    // Récupérer le message d'erreur
                    String errorMsg = "Erreur inconnue";
                    if (jsonObj.has("message")) {
                        errorMsg = jsonObj.get("message").getAsString();
                    } else if (jsonObj.get("error").isJsonPrimitive()) {
                        errorMsg = jsonObj.get("error").getAsString();
                    }
                    
                    logger.warning("Erreur lors de la création de la devise: " + errorMsg);
                    loadDevises(request, response, errorMsg, null);
                    return;
                }
            }
            
            logger.info("Devise créée avec succès");
            response.sendRedirect(request.getContextPath() + "/devises?success=created");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Format invalide pour le cours", e);
            loadDevises(request, response, "Format invalide pour le cours", null);
        } catch (java.time.format.DateTimeParseException e) {
            logger.log(Level.WARNING, "Format invalide pour la date", e);
            loadDevises(request, response, "Format invalide pour la date", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la devise", e);
            loadDevises(request, response, "Erreur serveur lors de la création: " + e.getMessage(), null);
        }
    }
    
    /**
     * Gérer la validation d'une demande de devise
     */
    private void handleValider(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String idStr = request.getParameter("id");
        String dateValidation = request.getParameter("dateValidation");
        
        if (idStr == null || dateValidation == null) {
            loadDevises(request, response, "ID et dateValidation requis", null);
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            java.time.LocalDate date = java.time.LocalDate.parse(dateValidation);
            
            // Valider la devise avec la date de validation
            JsonElement result = deviseService.valider(id, date);
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject() && result.getAsJsonObject().has("error")) {
                String errorMsg = result.getAsJsonObject().get("error").getAsString();
                logger.warning("Erreur lors de la validation de la devise: " + errorMsg);
                loadDevises(request, response, errorMsg, null);
                return;
            }
            
            logger.info("Devise validée");
            response.sendRedirect(request.getContextPath() + "/devises?success=validated");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "ID invalide", e);
            loadDevises(request, response, "ID invalide", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation de la devise", e);
            loadDevises(request, response, "Erreur serveur lors de la validation: " + e.getMessage(), null);
        }
    }
    
    /**
     * Gérer le refus d'une demande de devise
     */
    private void handleRefuser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String idStr = request.getParameter("id");
        String dateRefus = request.getParameter("dateRefus");
        
        if (idStr == null || dateRefus == null) {
            loadDevises(request, response, "ID et dateRefus requis", null);
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            
            // Refuser la devise
            JsonElement result = deviseService.refuser(id);
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject() && result.getAsJsonObject().has("error")) {
                String errorMsg = result.getAsJsonObject().get("error").getAsString();
                logger.warning("Erreur lors du refus de la devise: " + errorMsg);
                loadDevises(request, response, errorMsg, null);
                return;
            }
            
            logger.info("Devise refusée");
            response.sendRedirect(request.getContextPath() + "/devises?success=refused");
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "ID invalide", e);
            loadDevises(request, response, "ID invalide", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du refus de la devise", e);
            loadDevises(request, response, "Erreur serveur lors du refus: " + e.getMessage(), null);
        }
    }
    
    /**
     * Charger la page des devises avec messages d'erreur ou de succès
     */
    private void loadDevises(HttpServletRequest request, HttpServletResponse response, 
                               String error, String success) throws ServletException, IOException {
        try {
            JsonElement devisesActivesElement = deviseService.getActiveDevises();
            JsonElement demandesEnAttenteElement = deviseService.getDevisesEnAttente();
            
            // Convertir JsonArray en List<Map> pour que JSTL puisse l'itérer
            List<java.util.Map<String, Object>> devisesActives = jsonArrayToList(devisesActivesElement.getAsJsonArray());
            List<java.util.Map<String, Object>> demandesEnAttente = jsonArrayToList(demandesEnAttenteElement.getAsJsonArray());
            
            request.setAttribute("devisesActives", devisesActives);
            request.setAttribute("demandesEnAttente", demandesEnAttente);
            
            if (error != null) {
                request.setAttribute("error", error);
            }
            if (success != null) {
                request.setAttribute("success", success);
            }
            
            request.getRequestDispatcher("/WEB-INF/jsp/devises/devises.jsp").forward(request, response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de la page des devises", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
    
    /**
     * Convertir un JsonArray en List<Map<String, Object>> pour JSTL
     */
    private List<java.util.Map<String, Object>> jsonArrayToList(com.google.gson.JsonArray array) {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        
        for (com.google.gson.JsonElement element : array) {
            com.google.gson.JsonObject obj = element.getAsJsonObject();
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            
            for (String key : obj.keySet()) {
                com.google.gson.JsonElement value = obj.get(key);
                if (value.isJsonPrimitive()) {
                    com.google.gson.JsonPrimitive primitive = value.getAsJsonPrimitive();
                    if (primitive.isString()) {
                        map.put(key, primitive.getAsString());
                    } else if (primitive.isNumber()) {
                        map.put(key, primitive.getAsNumber());
                    } else if (primitive.isBoolean()) {
                        map.put(key, primitive.getAsBoolean());
                    }
                } else {
                    map.put(key, value.toString());
                }
            }
            list.add(map);
        }
        
        return list;
    }
}
