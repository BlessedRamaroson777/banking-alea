package mg.itu.rest;

import mg.itu.service.AuthService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification et la gestion de session
 * Endpoints : /api/auth
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRestController {
    
    private final AuthService authService = new AuthService();
    
    /**
     * POST /api/auth/login
     * Connecter un utilisateur et définir son niveau de rôle
     */
    @POST
    @Path("/login")
    public Response login(Map<String, Object> credentials) {
        try {
            // Extraire les informations de connexion
            Integer userId = credentials.get("userId") != null 
                ? Integer.parseInt(credentials.get("userId").toString()) 
                : null;
            Integer roleLevel = credentials.get("roleLevel") != null 
                ? Integer.parseInt(credentials.get("roleLevel").toString()) 
                : null;
            
            if (userId == null || roleLevel == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("userId et roleLevel sont obligatoires"))
                        .build();
            }
            
            // Connecter via le service (charge automatiquement les permissions)
            Map<String, Object> response = authService.login(userId, roleLevel);
            return Response.ok(response).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la connexion: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * POST /api/auth/logout
     * Déconnecter l'utilisateur et libérer le contexte
     */
    @POST
    @Path("/logout")
    public Response logout() {
        try {
            // Déconnecter via le service (vide automatiquement le cache)
            Map<String, Object> response = authService.logout();
            return Response.ok(response).build();
            
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors de la déconnexion: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * GET /api/auth/session
     * Obtenir les informations de la session courante
     */
    @GET
    @Path("/session")
    public Response getSession() {
        Map<String, Object> response = authService.getSession();
        return Response.ok(response).build();
    }
    
    /**
     * GET /api/auth/permissions/{table}/{action}
     * Vérifier si l'utilisateur connecté a une permission spécifique
     */
    @GET
    @Path("/permissions/{table}/{action}")
    public Response checkPermission(@PathParam("table") String table, @PathParam("action") String action) {
        Map<String, Object> response = authService.checkPermission(table, action);
        return Response.ok(response).build();
    }
    
    /**
     * POST /api/auth/refresh-permissions
     * Recharger les permissions (utile après modification de actions_roles)
     */
    @POST
    @Path("/refresh-permissions")
    public Response refreshPermissions() {
        try {
            Map<String, Object> response = authService.refreshPermissions();
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur lors du rechargement des permissions: " + e.getMessage()))
                    .build();
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
