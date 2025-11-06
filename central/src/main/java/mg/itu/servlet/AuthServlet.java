package mg.itu.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.itu.service.rest.AuthService;

/**
 * Servlet pour gérer l'authentification
 */
@WebServlet(urlPatterns = {"/", "/auth/*"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AuthServlet.class.getName());
    private final AuthService authService;
    
    public AuthServlet() {
        this.authService = new AuthService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Afficher le formulaire de connexion
                request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
            } else if (pathInfo.equals("/logout")) {
                // Déconnexion
                handleLogout(request, response);
            } else if (pathInfo.equals("/session")) {
                // Récupérer la session courante
                handleGetSession(request, response);
            } else if (pathInfo.startsWith("/permissions/")) {
                // Vérifier une permission: /auth/permissions/{table}/{action}
                handleCheckPermission(request, response, pathInfo);
            } else if (pathInfo.equals("/refresh-permissions")) {
                // Recharger les permissions
                handleRefreshPermissions(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors du traitement de la requête GET", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/login")) {
            handleLogin(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Gérer la connexion
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        
        if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Nom d'utilisateur et mot de passe requis");
            request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
            return;
        }
        
        try {
            // Appeler le service de connexion
            java.util.Map<String, Object> loginResult = null;
            try {
                loginResult = authService.login(name, password);
            } catch (IllegalArgumentException e) {
                // Erreur de validation (ex: utilisateur n'existe pas)
                logger.log(Level.WARNING, "Échec de connexion pour l'utilisateur: " + name, e);
                request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
                request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
                return;
            } catch (IOException e) {
                // Erreur de communication avec les services
                logger.log(Level.SEVERE, "Erreur de communication avec les services pour: " + name, e);
                request.setAttribute("error", "Erreur de connexion au serveur. Le service est peut-être indisponible.");
                request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
                return;
            }
            
            if (loginResult == null) {
                request.setAttribute("error", "Erreur inconnue lors de la connexion");
                request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
                return;
            }
            
            // Récupérer les informations de l'utilisateur
            com.google.gson.JsonElement utilisateurElement = (com.google.gson.JsonElement) loginResult.get("utilisateur");
            if (utilisateurElement != null && utilisateurElement.isJsonObject()) {
                com.google.gson.JsonObject utilisateur = utilisateurElement.getAsJsonObject();
                
                // Stocker les informations dans la session HTTP locale
                HttpSession session = request.getSession(true);
                session.setAttribute("name", name);
                session.setAttribute("loggedIn", true);
                
                // Stocker l'ID de l'utilisateur et le nom
                if (utilisateur.has("id")) {
                    session.setAttribute("utilisateurId", utilisateur.get("id").getAsInt());
                }
                if (utilisateur.has("nom")) {
                    session.setAttribute("utilisateurNom", utilisateur.get("nom").getAsString());
                }
                if (utilisateur.has("niveau")) {
                    session.setAttribute("utilisateurNiveau", utilisateur.get("niveau").getAsInt());
                }
            }
            
            // Récupérer les ActionRole depuis l'API Courant basés sur le niveau de l'utilisateur
            HttpSession session = request.getSession();
            Integer utilisateurNiveau = (Integer) session.getAttribute("utilisateurNiveau");
            
            if (utilisateurNiveau != null) {
                try {
                    // Appeler l'API Courant pour récupérer les ActionRole filtrés par niveau
                    mg.itu.service.rest.ActionRoleService actionRoleService = new mg.itu.service.rest.ActionRoleService();
                    com.google.gson.JsonElement actionRolesJson = actionRoleService.getByNiveau(utilisateurNiveau);
                    
                    if (actionRolesJson != null && actionRolesJson.isJsonArray()) {
                        com.google.gson.JsonArray actionRolesArray = actionRolesJson.getAsJsonArray();
                        
                        // Convertir JsonArray en List<ActionRole>
                        java.util.List<mg.itu.dto.courant.ActionRole> actionRoles = new java.util.ArrayList<>();
                        for (com.google.gson.JsonElement element : actionRolesArray) {
                            if (element.isJsonObject()) {
                                com.google.gson.JsonObject roleObj = element.getAsJsonObject();
                                mg.itu.dto.courant.ActionRole role = new mg.itu.dto.courant.ActionRole();
                                
                                if (roleObj.has("id")) {
                                    role.setId(roleObj.get("id").getAsInt());
                                }
                                if (roleObj.has("nomTable")) {
                                    role.setNomTable(roleObj.get("nomTable").getAsString());
                                }
                                if (roleObj.has("nomAction")) {
                                    role.setNomAction(roleObj.get("nomAction").getAsString());
                                }
                                if (roleObj.has("roleMinimum")) {
                                    role.setRoleMinimum(roleObj.get("roleMinimum").getAsInt());
                                }
                                
                                actionRoles.add(role);
                            }
                        }
                        
                        // Stocker les ActionRoles dans la session HTTP
                        session.setAttribute("actionRoles", actionRoles);
                        logger.info("ActionRoles stockés dans la session pour niveau " + utilisateurNiveau + ": " + actionRoles.size() + " rôles");
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Erreur lors de la récupération des ActionRoles", e);
                    // Continuer sans ActionRoles plutôt que d'échouer le login
                }
            }
            
            // Rediriger vers la page d'accueil
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erreur de validation générale lors de la connexion", e);
            request.setAttribute("error", "Échec de la connexion: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur IO lors de la connexion", e);
            request.setAttribute("error", "Erreur de communication avec le serveur. Veuillez réessayer.");
            request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur inattendue lors de la connexion", e);
            request.setAttribute("error", "Erreur inattendue: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(request, response);
        }
    }
    
    /**
     * Gérer la déconnexion
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Appeler le service de déconnexion
        authService.logout();
        
        // Invalider la session HTTP locale
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Rediriger vers la page de connexion
        response.sendRedirect(request.getContextPath() + "/auth");
    }
    
    /**
     * Récupérer la session courante
     */
    private void handleGetSession(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        JsonElement result = authService.getSession();
        
        request.setAttribute("sessionData", result);
        request.getRequestDispatcher("/WEB-INF/jsp/auth/session.jsp").forward(request, response);
    }
    
    /**
     * Vérifier une permission
     */
    private void handleCheckPermission(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException, ServletException {
        // Extraire table et action de /permissions/{table}/{action}
        String[] parts = pathInfo.split("/");
        if (parts.length < 4) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format: /permissions/{table}/{action}");
            return;
        }
        
        String table = parts[2];
        String action = parts[3];
        
        JsonElement result = authService.checkPermission(table, action);
        
        request.setAttribute("permissionData", result);
        request.setAttribute("table", table);
        request.setAttribute("action", action);
        request.getRequestDispatcher("/WEB-INF/jsp/auth/permission.jsp").forward(request, response);
    }
    
    /**
     * Recharger les permissions
     */
    private void handleRefreshPermissions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        authService.refreshPermissions();
        
        // Rediriger vers la page de session ou la page précédente
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/auth/session");
        }
    }
}
