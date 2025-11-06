package mg.itu.service;

import mg.itu.model.DeviseModification;
import mg.itu.security.PermissionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service métier pour DeviseModification
 * Gère les opérations CRUD et la logique métier avec vérification des permissions
 */
public class DeviseModificationService {
    
    private static final String TABLE_NAME = "devises_modifications";
    
    /**
     * Créer une proposition de modification
     */
    public DeviseModification create(DeviseModification modification) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "CREATE");
        return modification.create();
    }
    
    /**
     * Récupérer une proposition par ID
     */
    public DeviseModification getById(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        return DeviseModification.getById(DeviseModification.class, id);
    }
    
    /**
     * Mettre à jour une proposition
     */
    public DeviseModification update(DeviseModification modification) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "UPDATE");
        return modification.update();
    }
    
    /**
     * Supprimer une proposition
     */
    public boolean delete(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "DELETE");
        return DeviseModification.deleteById(DeviseModification.class, id);
    }
    
    /**
     * Récupérer toutes les propositions avec filtres optionnels
     */
    public List<DeviseModification> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        return DeviseModification.getAll(DeviseModification.class, filters, orderBy, limit, offset);
    }
    
    /**
     * Récupérer toutes les propositions
     */
    public List<DeviseModification> getAll() throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        return DeviseModification.getAll(DeviseModification.class);
    }
    
    /**
     * Récupérer toutes les propositions en attente
     */
    public List<DeviseModification> getModificationsEnAttente() throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("statutValidation", 1); // En attente
        return DeviseModification.getAll(DeviseModification.class, filters, "dateProposition", null, null);
    }
    
    /**
     * Récupérer toutes les propositions pour une devise donnée
     */
    public List<DeviseModification> getModificationsByDeviseId(int deviseId) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("deviseId", deviseId);
        return DeviseModification.getAll(DeviseModification.class, filters, "dateProposition", null, null);
    }
    
    /**
     * Récupérer toutes les propositions en attente pour une devise donnée
     */
    public List<DeviseModification> getModificationsEnAttenteByDeviseId(int deviseId) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("deviseId", deviseId);
        filters.put("statutValidation", 1); // En attente
        return DeviseModification.getAll(DeviseModification.class, filters, "dateProposition", null, null);
    }
    
    /**
     * Valider une proposition de modification
     * Nécessite la permission VALIDATE sur devises_modifications
     * @param dateValidation Date de validation à appliquer (optionnel)
     */
    public DeviseModification validerModification(int id, LocalDate dateValidation) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "VALIDATE");
        DeviseModification modification = DeviseModification.getById(DeviseModification.class, id);
        if (modification == null) {
            throw new SQLException("Proposition de modification non trouvée");
        }
        return modification.validerModification(dateValidation);
    }
    
    /**
     * Refuser une proposition de modification
     * Nécessite la permission VALIDATE sur devises_modifications
     */
    public DeviseModification refuserModification(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "VALIDATE");
        DeviseModification modification = DeviseModification.getById(DeviseModification.class, id);
        if (modification == null) {
            throw new SQLException("Proposition de modification non trouvée");
        }
        return modification.refuserModification();
    }
    
    /**
     * Proposer une modification pour une devise
     * Nécessite la permission CREATE sur devises_modifications
     */
    public DeviseModification proposeModification(int deviseId, DeviseModification propositionData) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "CREATE");
        return DeviseModification.proposeModification(deviseId, propositionData);
    }
}
