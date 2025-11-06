package mg.itu.rest;

import mg.itu.model.CodeStatutDevise;
import mg.itu.service.CodeStatutDeviseService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour CodeStatutDevise
 * Endpoints : /api/codes-statuts
 */
@Path("/codes-statuts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CodeStatutDeviseRestController {
    
    private final CodeStatutDeviseService service = new CodeStatutDeviseService();
    
    @POST
    public Response create(CodeStatutDevise code) {
        try {
            if (code == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Le code statut ne peut pas être null"))
                        .build();
            }
            
            CodeStatutDevise created = service.create(code);
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
            CodeStatutDevise code = service.getById(id);
            return Response.ok(code).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Code statut non trouvé"))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, CodeStatutDevise code) {
        try {
            if (code == null || id != code.getId()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Données invalides"))
                        .build();
            }
            
            CodeStatutDevise updated = service.update(code);
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
    public Response getAll() {
        try {
            List<CodeStatutDevise> codes = service.getAll();
            return Response.ok(codes).build();
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
