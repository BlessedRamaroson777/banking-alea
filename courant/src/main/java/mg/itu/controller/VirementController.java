package mg.itu.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mg.itu.models.ActionRole;
import mg.itu.models.Virement;
import mg.itu.services.VirementService;

@Path("/virements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VirementController {

    private final VirementService virementService = new VirementService();

    /**
     * GET /api/virements
     * Récupère tous les virements
     */
    @GET
    public Response getAll() {
        try {
            List<Virement> virements = virementService.getAll();
            return Response.ok(virements).build();
        } catch (Exception e) {
            return serverError(e.getMessage());
        }
    }

    /**
     * GET /api/virements/statut?code=1
     * Récupère les virements par statut
     */
    @GET
    @Path("/statut")
    public Response getByStatut(@QueryParam("code") Integer statutCode) {
        try {
            if (statutCode == null) {
                return badRequest("Le paramètre 'code' est requis");
            }
            List<Virement> virements = virementService.getByStatut(statutCode);
            return Response.ok(virements).build();
        } catch (Exception e) {
            return serverError(e.getMessage());
        }
    }

    /**
     * POST /api/virements/{id}/valider
     * Body: { "utilisateurId": 5, "dateValidation": "2025-01-01T12:00:00", "actionRoles": [...] }
     */
    @POST
    @Path("/{id}/valider")
    public Response valider(@PathParam("id") Integer id, Map<String, Object> body) {
        try {
            Integer utilisateurId = toInteger(body.get("utilisateurId"));
            LocalDateTime dateValidation = toDateTime(body.get("dateValidation"));
            List<ActionRole> actionRoles = toActionRoles(body.get("actionRoles"));

            Virement v = virementService.valider(id, utilisateurId, dateValidation, actionRoles);
            return Response.ok(v).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError(e.getMessage());
        }
    }

    /**
     * POST /api/virements/{id}/refuser
     * Body: { "utilisateurId": 5, "dateValidation": "2025-01-01T12:00:00", "actionRoles": [...] }
     */
    @POST
    @Path("/{id}/refuser")
    public Response refuser(@PathParam("id") Integer id, Map<String, Object> body) {
        try {
            Integer utilisateurId = toInteger(body.get("utilisateurId"));
            LocalDateTime dateValidation = toDateTime(body.get("dateValidation"));
            List<ActionRole> actionRoles = toActionRoles(body.get("actionRoles"));

            Virement v = virementService.refuser(id, utilisateurId, dateValidation, actionRoles);
            return Response.ok(v).build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            return serverError(e.getMessage());
        }
    }

    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(o.toString());
    }

    private LocalDateTime toDateTime(Object o) {
        if (o == null) return LocalDateTime.now();
        return LocalDateTime.parse(o.toString());
    }

    private List<ActionRole> toActionRoles(Object o) {
        List<ActionRole> roles = new ArrayList<>();
        if (o == null) return roles;
        if (o instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) {
                    ActionRole ar = new ActionRole();
                    Object nomTable = m.get("nomTable");
                    Object nomAction = m.get("nomAction");
                    Object roleMinimum = m.get("roleMinimum");
                    if (nomTable != null) ar.setNomTable(nomTable.toString());
                    if (nomAction != null) ar.setNomAction(nomAction.toString());
                    if (roleMinimum != null) ar.setRoleMinimum(Integer.parseInt(roleMinimum.toString()));
                    roles.add(ar);
                }
            }
        }

        return roles;
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", true, "message", message))
                .build();
    }

    private Response serverError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", true, "message", message))
                .build();
    }
}
