package mg.itu.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
import jakarta.servlet.http.HttpSession;
import mg.itu.dto.courant.ActionRole;
import mg.itu.service.rest.CompteService;
import mg.itu.service.rest.VirementService;

/**
 * Servlet pour gérer les virements
 */
@WebServlet(urlPatterns = {"/virements", "/virements/*"})
public class VirementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VirementServlet.class.getName());
    private final CompteService compteService;
    private final VirementService virementService;
    
    public VirementServlet() {
        this.compteService = new CompteService();
        this.virementService = new VirementService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        // Vérifier la session
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Charger tous les virements
                JsonElement allVirements = virementService.getAll();
                request.setAttribute("allVirements", allVirements);
                
                // Charger les virements en attente (statut = 1)
                JsonElement virementsEnAttente = virementService.getByStatut(1);
                request.setAttribute("virementsEnAttente", virementsEnAttente);
                
                // Charger les virements validés (statut = 11)
                JsonElement virementsValides = virementService.getByStatut(11);
                request.setAttribute("virementsValides", virementsValides);
                
                // Charger tous les comptes disponibles
                JsonElement comptes = compteService.getAll();
                request.setAttribute("comptes", comptes);
                
                // Charger les devises actuellement actives
                mg.itu.service.rest.DeviseService deviseService = new mg.itu.service.rest.DeviseService();
                JsonElement devisesActives = deviseService.getCurrentlyActiveDevises();
                request.setAttribute("devisesActives", devisesActives);
                
                // Récupérer l'utilisateur de la session
                HttpSession session = request.getSession();
                Integer utilisateurId = (Integer) session.getAttribute("utilisateurId");
                request.setAttribute("utilisateurId", utilisateurId);
                
                // Afficher le formulaire de virement avec la liste
                request.getRequestDispatcher("/WEB-INF/jsp/virements/virement.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du traitement de la requête GET", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        // Vérifier la session
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        
        try {
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/creer")) {
                // Créer un virement
                handleCreerVirement(request, response);
            } else if (pathInfo.matches("/\\d+/valider")) {
                // Valider un virement: /virements/{id}/valider
                int virementId = extractIdFromPath(pathInfo);
                handleValiderVirement(request, response, virementId);
            } else if (pathInfo.matches("/\\d+/refuser")) {
                // Refuser un virement: /virements/{id}/refuser
                int virementId = extractIdFromPath(pathInfo);
                handleRefuserVirement(request, response, virementId);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du traitement de la requête POST", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
    
    /**
     * Gérer la création d'un virement
     */
    private void handleCreerVirement(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        try {
            // Récupérer l'utilisateur de la session
            HttpSession session = request.getSession();
            Integer utilisateurId = (Integer) session.getAttribute("utilisateurId");
            
            if (utilisateurId == null) {
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
            
            // Récupérer les paramètres
            Integer compteEnvoyeurId = parseInteger(request.getParameter("compteEnvoyeurId"));
            Integer compteDestinataireId = parseInteger(request.getParameter("compteDestinataireId"));
            BigDecimal montant = parseBigDecimal(request.getParameter("montant"));
            LocalDateTime dateCreation = parseDateTime(request.getParameter("dateCreation"));
            LocalDateTime dateEffet = parseDateTime(request.getParameter("dateEffet"));
            Integer changeId = parseInteger(request.getParameter("changeId"));
            
            // Récupérer les ActionRoles depuis la session HTTP
            @SuppressWarnings("unchecked")
            List<ActionRole> actionRoles = (List<ActionRole>) session.getAttribute("actionRoles");
            if (actionRoles == null) {
                actionRoles = new ArrayList<>();
                logger.warning("ActionRoles non trouvés dans la session, utilisation d'une liste vide");
            }
            
            // Validation
            if (compteEnvoyeurId == null || compteDestinataireId == null || montant == null) {
                request.setAttribute("error", "Paramètres manquants: compteEnvoyeurId, compteDestinataireId et montant sont requis");
                response.sendRedirect(request.getContextPath() + "/virements");
                return;
            }
            
            // Appeler le service
            JsonElement result = compteService.virer(
                compteEnvoyeurId,
                compteDestinataireId,
                montant,
                dateCreation,
                dateEffet,
                utilisateurId,
                changeId,
                actionRoles
            );
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject()) {
                com.google.gson.JsonObject resultObj = result.getAsJsonObject();
                if (resultObj.has("error") && resultObj.get("error").getAsBoolean()) {
                    // Il y a une erreur dans la réponse
                    String errorMessage = resultObj.has("message") 
                        ? resultObj.get("message").getAsString() 
                        : "Erreur inconnue lors de la création du virement";
                    session.setAttribute("error", errorMessage);
                    response.sendRedirect(request.getContextPath() + "/virements");
                    return;
                }
            }
            
            // Rediriger avec succès
            session.setAttribute("successMessage", "Virement créé avec succès");
            response.sendRedirect(request.getContextPath() + "/virements");
            
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erreur de validation lors de la création du virement", e);
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la création du virement", e);
            request.getSession().setAttribute("error", "Erreur lors de la création du virement: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur inattendue lors de la création du virement", e);
            request.getSession().setAttribute("error", "Erreur inattendue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        }
    }
    
    /**
     * Gérer la validation d'un virement
     */
    private void handleValiderVirement(HttpServletRequest request, HttpServletResponse response, int virementId) 
            throws IOException, ServletException {
        try {
            // Récupérer l'utilisateur de la session
            HttpSession session = request.getSession();
            Integer utilisateurId = (Integer) session.getAttribute("utilisateurId");
            
            if (utilisateurId == null) {
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
            
            // Récupérer la date de validation
            LocalDateTime dateValidation = parseDateTime(request.getParameter("dateValidation"));
            if (dateValidation == null) {
                session.setAttribute("error", "La date de validation est requise");
                response.sendRedirect(request.getContextPath() + "/virements");
                return;
            }
            
            // Récupérer les ActionRoles depuis la session HTTP
            @SuppressWarnings("unchecked")
            List<ActionRole> actionRoles = (List<ActionRole>) session.getAttribute("actionRoles");
            if (actionRoles == null) {
                actionRoles = new ArrayList<>();
                logger.warning("ActionRoles non trouvés dans la session, utilisation d'une liste vide");
            }
            
            // Appeler le service
            JsonElement result = virementService.valider(virementId, utilisateurId, dateValidation, actionRoles);
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject()) {
                com.google.gson.JsonObject resultObj = result.getAsJsonObject();
                if (resultObj.has("error") && resultObj.get("error").getAsBoolean()) {
                    // Il y a une erreur dans la réponse
                    String errorMessage = resultObj.has("message") 
                        ? resultObj.get("message").getAsString() 
                        : "Erreur inconnue lors de la validation du virement";
                    session.setAttribute("error", errorMessage);
                    response.sendRedirect(request.getContextPath() + "/virements");
                    return;
                }
            }
            
            // Rediriger avec succès
            session.setAttribute("successMessage", "Virement validé avec succès");
            response.sendRedirect(request.getContextPath() + "/virements");
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation du virement", e);
            request.getSession().setAttribute("error", "Erreur lors de la validation du virement: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur inattendue lors de la validation du virement", e);
            request.getSession().setAttribute("error", "Erreur inattendue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        }
    }
    
    /**
     * Gérer le refus d'un virement
     */
    private void handleRefuserVirement(HttpServletRequest request, HttpServletResponse response, int virementId) 
            throws IOException, ServletException {
        try {
            // Récupérer l'utilisateur de la session
            HttpSession session = request.getSession();
            Integer utilisateurId = (Integer) session.getAttribute("utilisateurId");
            
            if (utilisateurId == null) {
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
            
            // Récupérer la date de refus
            LocalDateTime dateValidation = parseDateTime(request.getParameter("dateValidation"));
            if (dateValidation == null) {
                session.setAttribute("error", "La date de refus est requise");
                response.sendRedirect(request.getContextPath() + "/virements");
                return;
            }
            
            // Récupérer les ActionRoles depuis la session HTTP
            @SuppressWarnings("unchecked")
            List<ActionRole> actionRoles = (List<ActionRole>) session.getAttribute("actionRoles");
            if (actionRoles == null) {
                actionRoles = new ArrayList<>();
                logger.warning("ActionRoles non trouvés dans la session, utilisation d'une liste vide");
            }
            
            // Appeler le service
            JsonElement result = virementService.refuser(virementId, utilisateurId, dateValidation, actionRoles);
            
            // Vérifier si la réponse contient une erreur
            if (result != null && result.isJsonObject()) {
                com.google.gson.JsonObject resultObj = result.getAsJsonObject();
                if (resultObj.has("error") && resultObj.get("error").getAsBoolean()) {
                    // Il y a une erreur dans la réponse
                    String errorMessage = resultObj.has("message") 
                        ? resultObj.get("message").getAsString() 
                        : "Erreur inconnue lors du refus du virement";
                    session.setAttribute("error", errorMessage);
                    response.sendRedirect(request.getContextPath() + "/virements");
                    return;
                }
            }
            
            // Rediriger avec succès
            session.setAttribute("successMessage", "Virement refusé avec succès");
            response.sendRedirect(request.getContextPath() + "/virements");
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du refus du virement", e);
            request.getSession().setAttribute("error", "Erreur lors du refus du virement: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur inattendue lors du refus du virement", e);
            request.getSession().setAttribute("error", "Erreur inattendue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/virements");
        }
    }
    
    /**
     * Vérifier si l'utilisateur est connecté
     */
    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("loggedIn"));
    }
    
    /**
     * Extraire l'ID depuis le path
     */
    private int extractIdFromPath(String pathInfo) {
        String[] parts = pathInfo.split("/");
        return Integer.parseInt(parts[1]);
    }
    
    /**
     * Parser un Integer depuis une String
     */
    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de nombre invalide: " + value);
        }
    }
    
    /**
     * Parser un BigDecimal depuis une String
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format de montant invalide: " + value);
        }
    }
    
    /**
     * Parser un LocalDateTime depuis une String
     */
    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide: " + value);
        }
    }
    
    /**
     * Parser les ActionRoles depuis les paramètres de la requête
     */
    private List<ActionRole> parseActionRoles(HttpServletRequest request) {
        List<ActionRole> roles = new ArrayList<>();
        
        String[] nomTables = request.getParameterValues("actionRole.nomTable");
        String[] nomActions = request.getParameterValues("actionRole.nomAction");
        String[] roleMinimums = request.getParameterValues("actionRole.roleMinimum");
        
        if (nomTables != null && nomActions != null && roleMinimums != null) {
            int length = Math.min(Math.min(nomTables.length, nomActions.length), roleMinimums.length);
            for (int i = 0; i < length; i++) {
                ActionRole role = new ActionRole();
                role.setNomTable(nomTables[i]);
                role.setNomAction(nomActions[i]);
                role.setRoleMinimum(Integer.parseInt(roleMinimums[i]));
                roles.add(role);
            }
        }
        
        return roles;
    }
}
