package mg.itu.controller;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mg.itu.models.ActionRole;
import mg.itu.services.ActionRoleService;

@Path("/actionroles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActionRoleController {

    private final ActionRoleService actionRoleService = new ActionRoleService();

    /**
     * GET /actionroles
     * Récupérer tous les ActionRole
     */
    @GET
    public Response getAll() {
        try {
            List<ActionRole> actionRoles = actionRoleService.getAll();
            return Response.ok(actionRoles).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * GET /actionroles/niveau/{niveau}
     * Récupérer les ActionRole accessibles pour un niveau donné
     * (roleMinimum <= niveau)
     */
    @GET
    @Path("/niveau/{niveau}")
    public Response getByNiveau(@PathParam("niveau") Integer niveau) {
        try {
            if (niveau == null || niveau < 1) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Niveau invalide\"}")
                        .build();
            }
            
            List<ActionRole> actionRoles = actionRoleService.getByNiveauUtilisateur(niveau);
            return Response.ok(actionRoles).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
