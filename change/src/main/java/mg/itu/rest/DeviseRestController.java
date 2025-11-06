package mg.itu.rest;

import mg.itu.model.Devise;
import mg.itu.model.DeviseModification;
import mg.itu.service.DeviseService;
import mg.itu.service.DeviseModificationService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour Devise
 * Endpoints : /api/devises
 */
@Path("/devises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviseRestController {
    
    private final DeviseService deviseService = new DeviseService();
    private final DeviseModificationService modificationService = new DeviseModificationService();
    
    @POST
    public Response create(Devise devise) {
        try {
            if (devise == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("La devise ne peut pas être null"))
                        .build();
            }
            
            Devise created = deviseService.create(devise);
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
            Devise devise = deviseService.getById(id);
            return Response.ok(devise).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Devise non trouvée"))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") int id, Devise devise) {
        try {
            if (devise == null || id != devise.getId()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Données invalides"))
                        .build();
            }
            
            Devise updated = deviseService.update(devise);
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
            boolean deleted = deviseService.delete(id);
            return deleted ? Response.noContent().build() 
                          : Response.status(Response.Status.NOT_FOUND).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    @GET
    public Response getAll(@Context UriInfo uriInfo) {
        try {
            // Récupérer tous les query parameters
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            
            Map<String, Object> filters = new HashMap<>();
            String orderBy = null;
            Integer limit = null;
            Integer offset = null;
            
            // Parcourir tous les paramètres et construire les filtres
            for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue().get(0); // Prendre la première valeur
                
                // Paramètres spéciaux (ne sont pas des filtres)
                if (paramName.equals("orderBy")) {
                    orderBy = paramValue;
                    continue;
                } else if (paramName.equals("limit")) {
                    limit = Integer.parseInt(paramValue);
                    continue;
                } else if (paramName.equals("offset")) {
                    offset = Integer.parseInt(paramValue);
                    continue;
                }
                
                // Détecter le type de filtre à partir du suffixe du paramètre
                // Exemples: cours_gt, cours_lt, date_fin_null, code_like
                if (paramName.endsWith("_gt")) {
                    String fieldName = paramName.substring(0, paramName.length() - 3);
                    filters.put(fieldName + " >", parseValue(paramValue));
                } else if (paramName.endsWith("_lt")) {
                    String fieldName = paramName.substring(0, paramName.length() - 3);
                    filters.put(fieldName + " <", parseValue(paramValue));
                } else if (paramName.endsWith("_gte")) {
                    String fieldName = paramName.substring(0, paramName.length() - 4);
                    filters.put(fieldName + " >=", parseValue(paramValue));
                } else if (paramName.endsWith("_lte")) {
                    String fieldName = paramName.substring(0, paramName.length() - 4);
                    filters.put(fieldName + " <=", parseValue(paramValue));
                } else if (paramName.endsWith("_ne")) {
                    String fieldName = paramName.substring(0, paramName.length() - 3);
                    filters.put(fieldName + " !=", parseValue(paramValue));
                } else if (paramName.endsWith("_like")) {
                    String fieldName = paramName.substring(0, paramName.length() - 5);
                    filters.put(fieldName + " LIKE", paramValue);
                } else if (paramName.endsWith("_null")) {
                    String fieldName = paramName.substring(0, paramName.length() - 5);
                    boolean isNull = Boolean.parseBoolean(paramValue);
                    if (isNull) {
                        filters.put(fieldName + " IS NULL", null);
                    } else {
                        filters.put(fieldName + " IS NOT NULL", null);
                    }
                } else {
                    // Filtre d'égalité simple (par défaut)
                    filters.put(paramName, parseValue(paramValue));
                }
            }
            
            List<Devise> devises = deviseService.getAll(filters, orderBy, limit, offset);
            return Response.ok(devises).build();
            
        } catch (Exception e) {
            e.printStackTrace(); // Log dans la console WildFly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getClass().getName() + " - " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Parser une valeur de query parameter en détectant automatiquement le type
     */
    private Object parseValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // Essayer de parser en nombre
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            // Ce n'est pas un nombre, retourner comme String
            return value;
        }
    }
    
    /**
     * GET /api/devises/active
     * Récupère les devises actuellement actives
     */
    @GET
    @Path("/active")
    public Response getActiveDevises() {
        try {
            List<Devise> devises = deviseService.getCurrentlyActiveDevises();
            return Response.ok(devises).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * GET /api/devises/code/{code}
     * Récupère une devise par son code
     */
    @GET
    @Path("/code/{code}")
    public Response getByCode(@PathParam("code") String code) {
        try {
            Devise devise = deviseService.getByCode(code);
            return Response.ok(devise).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Devise non trouvée avec le code: " + code))
                    .build();
        }
    }
    
    /**
     * POST /api/devises/{id}/valider
     * Valider la création d'une devise (change le statut de "En attente" à "Valide")
     * Body: { "dateValidation": "2025-11-05" }
     */
    @POST
    @Path("/{id}/valider")
    public Response validerCreation(@PathParam("id") int id, Map<String, String> body) {
        try {
            // Récupérer la date de validation du body (optionnel)
            LocalDate dateValidation = null;
            if (body != null && body.containsKey("dateValidation")) {
                dateValidation = LocalDate.parse(body.get("dateValidation"));
            }
            
            Devise deviseValidee = deviseService.validerCreation(id, dateValidation);
            return Response.ok(deviseValidee).build();
            
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * POST /api/devises/{id}/refuser
     * Refuser la création d'une devise (change le statut de "En attente" à "Refuse")
     */
    @POST
    @Path("/{id}/refuser")
    public Response refuserCreation(@PathParam("id") int id) {
        try {
            Devise deviseRefusee = deviseService.refuserCreation(id);
            return Response.ok(deviseRefusee).build();
            
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * GET /api/devises/en-attente
     * Récupère toutes les devises en attente de validation
     */
    @GET
    @Path("/en-attente")
    public Response getDevisesEnAttente() {
        try {
            List<Devise> devises = deviseService.getDevisesEnAttente();
            return Response.ok(devises).build();
            
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * POST /api/devises/{id}/propose-modification
     * Proposer une modification d'une devise (crée une entrée en attente de validation)
     * Body: Objet DeviseModification avec les champs à modifier
     */
    @POST
    @Path("/{id}/propose-modification")
    public Response proposeModification(@PathParam("id") int id, DeviseModification propositionData) {
        try {
            if (propositionData == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Les données de modification ne peuvent pas être null"))
                        .build();
            }
            
            // Créer la proposition de modification via le service (vérifie les permissions)
            DeviseModification proposition = modificationService.proposeModification(id, propositionData);
            
            return Response.status(Response.Status.CREATED).entity(proposition).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(createErrorResponse(e.getMessage()))
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Erreur: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Format de données invalide: " + e.getMessage()))
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
