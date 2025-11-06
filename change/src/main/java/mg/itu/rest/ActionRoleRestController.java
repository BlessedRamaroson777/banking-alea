package mg.itu.rest;

import mg.itu.model.ActionRole;
import mg.itu.service.ActionRoleService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour ActionRole (Gestion des permissions)
 * Endpoints : /api/permissions
 */
@Path("/permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActionRoleRestController {
    
    private final ActionRoleService service = new ActionRoleService();
    
    @POST
    public Response create(ActionRole permission) {
        try {
            if (permission == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("La permission ne peut pas être null"))
                        .build();
            }
            
            ActionRole created = service.create(permission);
            return Response.status(Response.Status.CREATED).entity(created).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        try {
            ActionRole permission = service.getById(id);
            return Response.ok(permission).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Permission non trouvée"))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, ActionRole permission) {
        try {
            if (permission == null || id != permission.getId()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Données invalides"))
                        .build();
            }
            
            ActionRole updated = service.update(permission);
            return Response.ok(updated).build();
            
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            boolean deleted = service.delete(id);
            return deleted ? Response.noContent().build() 
                          : Response.status(Response.Status.NOT_FOUND).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    @GET
    public Response getAll(
            @QueryParam("table") String tableName,
            @QueryParam("role") Integer roleMinimum) {
        try {
            List<ActionRole> permissions;
            
            if (tableName != null && !tableName.trim().isEmpty()) {
                permissions = service.getPermissionsByTable(tableName);
            } else if (roleMinimum != null) {
                permissions = service.getPermissionsByRole(roleMinimum);
            } else {
                permissions = service.getAll();
            }
            
            return Response.ok(permissions).build();
            
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * GET /api/permissions/check?userRole=3&table=users&action=DELETE
     * Vérifie si un utilisateur a une permission
     */
    @GET
    @Path("/check")
    public Response checkPermission(
            @QueryParam("userRole") Integer userRole,
            @QueryParam("table") String tableName,
            @QueryParam("action") String action) {
        try {
            if (userRole == null || tableName == null || action == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Paramètres manquants: userRole, table, action"))
                        .build();
            }
            
            boolean hasPermission = service.checkPermission(userRole, tableName, action);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userRole", userRole);
            result.put("table", tableName);
            result.put("action", action);
            result.put("hasPermission", hasPermission);
            
            return Response.ok(result).build();
            
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
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
