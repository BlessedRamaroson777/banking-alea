package mg.itu.ejb;

import mg.itu.ejb.remote.ActionRoleRemote;
import mg.itu.model.ActionRole;
import mg.itu.service.ActionRoleService;
import jakarta.ejb.Stateless;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Bean EJB Stateless pour ActionRole
 */
@Stateless
public class ActionRoleEJB implements ActionRoleRemote {
    
    private final ActionRoleService service = new ActionRoleService();
    
    @Override
    public ActionRole create(ActionRole actionRole) throws SQLException {
        return service.create(actionRole);
    }
    
    @Override
    public ActionRole getById(int id) throws SQLException {
        return service.getById(id);
    }
    
    @Override
    public ActionRole update(ActionRole actionRole) throws SQLException {
        return service.update(actionRole);
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        return service.delete(id);
    }
    
    @Override
    public List<ActionRole> getAll() throws SQLException {
        return service.getAll();
    }
    
    @Override
    public List<ActionRole> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return service.getAll(filters, orderBy, limit, offset);
    }
    
    @Override
    public boolean checkPermission(int userRole, String tableName, String action) throws SQLException {
        return service.checkPermission(userRole, tableName, action);
    }
    
    @Override
    public List<ActionRole> getPermissionsByTable(String tableName) throws SQLException {
        return service.getPermissionsByTable(tableName);
    }
    
    @Override
    public List<ActionRole> getPermissionsByRole(int roleMinimum) throws SQLException {
        return service.getPermissionsByRole(roleMinimum);
    }
}
