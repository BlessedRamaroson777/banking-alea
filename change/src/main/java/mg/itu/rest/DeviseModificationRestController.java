package mg.itu.rest;

import mg.itu.model.DeviseModification;
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
 * Contrôleur REST pour DeviseModification
 * Endpoints : /api/devises-modifications
 */
@Path("/devises-modifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviseModificationRestController {
    
    private final DeviseModificationService modificationService = new DeviseModificationService();
    
    /**
     * POST /api/devises-modifications
     * Créer une proposition de modification
     */
    @POST
    public Response create(DeviseModification modification) {
        try {
            if (modification == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("La modification ne peut pas être null"))
                        .build();
            }
            
            DeviseModification created = modificationService.create(modification);
            return Response.status(Response.Status.CREATED).entity(created).build();
            
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
        }
    }
    
    /**
     * GET /api/devises-modifications/{id}
     * Récupérer une proposition par ID
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        try {
            DeviseModification modification = modificationService.getById(id);
            if (modification == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Proposition non trouvée"))
                        .build();
            }
            return Response.ok(modification).build();
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
     * GET /api/devises-modifications
     * Récupérer toutes les propositions avec filtres optionnels génériques
     */
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
                String paramValue = entry.getValue().get(0);
                
                // Paramètres spéciaux
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
                
                // Détecter le type de filtre à partir du suffixe
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
                    // Filtre d'égalité simple
                    filters.put(paramName, parseValue(paramValue));
                }
            }
            
            List<DeviseModification> modifications = modificationService.getAll(filters, orderBy, limit, offset);
            return Response.ok(modifications).build();
            
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
     * GET /api/devises-modifications/en-attente
     * Récupérer toutes les propositions en attente
     */
    @GET
    @Path("/en-attente")
    public Response getModificationsEnAttente() {
        try {
            List<DeviseModification> modifications = modificationService.getModificationsEnAttente();
            return Response.ok(modifications).build();
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
     * GET /api/devises-modifications/devise/{deviseId}
     * Récupérer toutes les propositions pour une devise donnée
     */
    @GET
    @Path("/devise/{deviseId}")
    public Response getByDeviseId(@PathParam("deviseId") int deviseId) {
        try {
            List<DeviseModification> modifications = modificationService.getModificationsByDeviseId(deviseId);
            return Response.ok(modifications).build();
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
     * POST /api/devises-modifications/{id}/valider
     * Valider une proposition de modification et l'appliquer à la devise
     * Body: { "dateValidation": "2025-11-05" }
     */
    @POST
    @Path("/{id}/valider")
    public Response validerModification(@PathParam("id") int id, Map<String, String> body) {
        try {
            // Récupérer la date de validation du body (optionnel)
            LocalDate dateValidation = null;
            if (body != null && body.containsKey("dateValidation")) {
                dateValidation = LocalDate.parse(body.get("dateValidation"));
            }
            
            DeviseModification modification = modificationService.validerModification(id, dateValidation);
            return Response.ok(modification).build();
            
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
     * POST /api/devises-modifications/{id}/refuser
     * Refuser une proposition de modification
     */
    @POST
    @Path("/{id}/refuser")
    public Response refuserModification(@PathParam("id") int id) {
        try {
            DeviseModification modification = modificationService.refuserModification(id);
            return Response.ok(modification).build();
            
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
     * DELETE /api/devises-modifications/{id}
     * Supprimer une proposition
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            boolean deleted = modificationService.delete(id);
            return deleted ? Response.noContent().build() 
                          : Response.status(Response.Status.NOT_FOUND).build();
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
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
