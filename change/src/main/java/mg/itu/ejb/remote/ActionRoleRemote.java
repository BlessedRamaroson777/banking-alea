package mg.itu.ejb.remote;

import mg.itu.model.ActionRole;
import jakarta.ejb.Remote;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface Remote pour le bean ActionRoleEJB
 */
@Remote
public interface ActionRoleRemote {
    
    ActionRole create(ActionRole actionRole) throws SQLException;
    
    ActionRole getById(int id) throws SQLException;
    
    ActionRole update(ActionRole actionRole) throws SQLException;
    
    boolean delete(int id) throws SQLException;
    
    List<ActionRole> getAll() throws SQLException;
    
    List<ActionRole> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException;
    
    /**
     * Vérifie si un utilisateur a la permission
     */
    boolean checkPermission(int userRole, String tableName, String action) throws SQLException;
    
    /**
     * Récupère toutes les permissions pour une table
     */
    List<ActionRole> getPermissionsByTable(String tableName) throws SQLException;
    
    /**
     * Récupère toutes les permissions pour un rôle
     */
    List<ActionRole> getPermissionsByRole(int roleMinimum) throws SQLException;
}
