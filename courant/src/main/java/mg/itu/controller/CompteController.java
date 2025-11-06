package mg.itu.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mg.itu.models.ActionRole;
import mg.itu.models.Virement;
import mg.itu.services.CompteService;

@Path("/comptes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompteController {

    private final CompteService compteService = new CompteService();

    /**
     * Récupérer tous les comptes
     * GET /api/comptes
     */
    @GET
    public Response getAll() {
        try {
            List<mg.itu.models.Compte> comptes = mg.itu.models.Compte.findAll(mg.itu.database.utils.DB.getConnection());
            return Response.ok(comptes).build();
        } catch (Exception e) {
            return serverError(e.getMessage());
        }
    }

    /**
     * Créer un virement entre deux comptes
     * POST /api/comptes/virements
     * Body JSON attendu:
     * {
     * "compteEnvoyeurId": 1,
     * "compteDestinataireId": 2,
     * "montant": 10000,
     * "dateCreation": "2025-01-01T12:00:00", // optionnel
     * "dateEffet": "2025-01-01T12:00:00",    // optionnel
     * "utilisateurId": 5,
     * "changeId": 1,
     * "actionRoles": [ {"nomTable":"Virement","nomAction":"CREER","roleMinimum":1} ]
     * }
     */
    @POST
    @Path("/virements")
    public Response virer(Map<String, Object> payload) {
        try {
            Integer compteEnvoyeurId = toInteger(payload.get("compteEnvoyeurId"));
            Integer compteDestinataireId = toInteger(payload.get("compteDestinataireId"));
            BigDecimal montant = toBigDecimal(payload.get("montant"));
            LocalDateTime dateCreation = toDateTime(payload.get("dateCreation"));
            LocalDateTime dateEffet = toDateTime(payload.get("dateEffet"));
            Integer utilisateurId = toInteger(payload.get("utilisateurId"));
            Integer changeId = toInteger(payload.get("changeId"));
            List<ActionRole> actionRoles = toActionRoles(payload.get("actionRoles"));

            Virement virement = compteService.virer(
                    compteEnvoyeurId,
                    compteDestinataireId,
                    montant,
                    dateCreation,
                    dateEffet,
                    utilisateurId,
                    changeId,
                    actionRoles
            );

            return Response.ok(virement).build();
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

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        return new BigDecimal(o.toString());
    }

    private LocalDateTime toDateTime(Object o) {
        if (o == null) return null;
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
