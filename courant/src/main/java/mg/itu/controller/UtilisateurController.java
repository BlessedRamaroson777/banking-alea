package mg.itu.controller;

import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mg.itu.models.Utilisateur;
import mg.itu.services.UtilisateurService;

@Path("/utilisateurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UtilisateurController {

    private final UtilisateurService utilisateurService = new UtilisateurService();

    /**
     * POST /api/utilisateurs/authenticate
     * Body: { "nom": "user", "motDePasse": "secret" }
     */
    @POST
    @Path("/authenticate")
    public Response authenticate(Map<String, Object> body) {
        try {
            String nom = body.get("nom") != null ? body.get("nom").toString() : null;
            String motDePasse = body.get("motDePasse") != null ? body.get("motDePasse").toString() : null;

            if (nom == null || motDePasse == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", true, "message", "Le nom et le mot de passe sont obligatoires"))
                        .build();
            }

            Utilisateur utilisateur = utilisateurService.authenticate(nom, motDePasse);
            return Response.ok(utilisateur).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", true, "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", true, "message", e.getMessage()))
                    .build();
        }
    }
}
