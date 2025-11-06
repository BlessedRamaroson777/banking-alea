package mg.itu.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mg.itu.model.Devise;
import mg.itu.security.PermissionManager;

/**
 * Service pour Devise avec logique métier spécifique et vérification des permissions
 */
public class DeviseService {
    
    private static final String TABLE_NAME = "devises";
    
    public Devise create(Devise devise) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "CREATE");
        return devise.create();
    }
    
    public Devise getById(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Devise devise = Devise.getById(Devise.class, id);
        if (devise == null) {
            throw new SQLException("Devise non trouvée avec l'ID: " + id);
        }
        return devise;
    }
    
    public Devise update(Devise devise) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "UPDATE");
        return devise.update();
    }
    
    public boolean delete(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "DELETE");
        return Devise.deleteById(Devise.class, id);
    }
    
    public List<Devise> getAll() throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        return Devise.getAll(Devise.class);
    }
    
    public List<Devise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        return Devise.getAll(Devise.class, filters, orderBy, limit, offset);
    }
    
    /**
     * Récupère toutes les devises actives à une date donnée
     */
    public List<Devise> getActiveDevises(LocalDate date) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        List<Devise> allDevises = Devise.getAll(Devise.class);
        List<Devise> activeDevises = new java.util.ArrayList<>();
        
        for (Devise devise : allDevises) {
            if (devise.isActiveAt(date)) {
                activeDevises.add(devise);
            }
        }
        
        return activeDevises;
    }
    
    /**
     * Récupère toutes les devises actuellement actives
     */
    public List<Devise> getCurrentlyActiveDevises() throws SQLException {
        return getActiveDevises(LocalDate.now());
    }
    
    /**
     * Récupère une devise par son code
     */
    public Devise getByCode(String code) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("code", code.toUpperCase());
        
        List<Devise> devises = Devise.getAll(Devise.class, filters, null, 1, null);
        if (devises.isEmpty()) {
            throw new SQLException("Devise non trouvée avec le code: " + code);
        }
        
        return devises.get(0);
    }
    
    /**
     * Valider la création d'une devise
     * Nécessite la permission VALIDATE
     */
    public Devise validerCreation(int id, LocalDate dateValidation) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "VALIDATE");
        Devise devise = getById(id);
        return devise.validerCreation(dateValidation);
    }
    
    /**
     * Refuser la création d'une devise
     * Nécessite la permission VALIDATE
     */
    public Devise refuserCreation(int id) throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "VALIDATE");
        Devise devise = getById(id);
        return devise.refuserCreation();
    }
    
    /**
     * Récupérer les devises en attente de validation
     */
    public List<Devise> getDevisesEnAttente() throws SQLException {
        PermissionManager.checkPermission(TABLE_NAME, "READ");
        Map<String, Object> filters = new HashMap<>();
        filters.put("statut_code", 1); // En attente (nom de colonne en snake_case)
        return Devise.getAll(Devise.class, filters, null, null, null);
    }
}
