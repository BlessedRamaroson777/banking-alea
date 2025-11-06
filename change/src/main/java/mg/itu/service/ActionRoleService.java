package mg.itu.service;

import mg.itu.model.ActionRole;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service pour ActionRole avec logique de gestion des permissions
 */
public class ActionRoleService {
    
    public ActionRole create(ActionRole actionRole) throws SQLException {
        return actionRole.create();
    }
    
    public ActionRole getById(int id) throws SQLException {
        ActionRole actionRole = ActionRole.getById(ActionRole.class, id);
        if (actionRole == null) {
            throw new SQLException("ActionRole non trouvé avec l'ID: " + id);
        }
        return actionRole;
    }
    
    public ActionRole update(ActionRole actionRole) throws SQLException {
        return actionRole.update();
    }
    
    public boolean delete(int id) throws SQLException {
        return ActionRole.deleteById(ActionRole.class, id);
    }
    
    public List<ActionRole> getAll() throws SQLException {
        return ActionRole.getAll(ActionRole.class);
    }
    
    public List<ActionRole> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return ActionRole.getAll(ActionRole.class, filters, orderBy, limit, offset);
    }
    
    /**
     * Vérifie si un utilisateur a la permission pour une action sur une table
     */
    public boolean checkPermission(int userRole, String tableName, String action) throws SQLException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("nomTable", tableName.toLowerCase());
        filters.put("nomAction", action.toUpperCase());
        
        List<ActionRole> permissions = getAll(filters, null, 1, null);
        
        if (permissions.isEmpty()) {
            // Pas de permission définie = accès refusé par défaut
            return false;
        }
        
        ActionRole permission = permissions.get(0);
        return permission.hasPermission(userRole);
    }
    
    /**
     * Récupère toutes les permissions pour une table
     */
    public List<ActionRole> getPermissionsByTable(String tableName) throws SQLException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("nomTable", tableName.toLowerCase());
        
        return getAll(filters, "nomAction", null, null);
    }
    
    /**
     * Récupère toutes les permissions pour un rôle donné
     */
    public List<ActionRole> getPermissionsByRole(int roleMinimum) throws SQLException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("roleMinimum", roleMinimum);
        
        return getAll(filters, "nomTable", null, null);
    }
}
