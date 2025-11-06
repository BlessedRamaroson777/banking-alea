package mg.itu.ejb;

import mg.itu.ejb.remote.DeviseModificationRemote;
import mg.itu.model.DeviseModification;
import mg.itu.service.DeviseModificationService;
import jakarta.ejb.Stateless;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * EJB Stateless pour DeviseModification
 * Expose les opérations métier via Remote
 */
@Stateless
public class DeviseModificationEJB implements DeviseModificationRemote {
    
    private final DeviseModificationService service = new DeviseModificationService();
    
    @Override
    public DeviseModification create(DeviseModification modification) throws SQLException {
        return service.create(modification);
    }
    
    @Override
    public DeviseModification getById(int id) throws SQLException {
        return service.getById(id);
    }
    
    @Override
    public DeviseModification update(DeviseModification modification) throws SQLException {
        return service.update(modification);
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        return service.delete(id);
    }
    
    @Override
    public List<DeviseModification> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return service.getAll(filters, orderBy, limit, offset);
    }
    
    @Override
    public List<DeviseModification> getAll() throws SQLException {
        return service.getAll();
    }
    
    @Override
    public List<DeviseModification> getModificationsEnAttente() throws SQLException {
        return service.getModificationsEnAttente();
    }
    
    @Override
    public List<DeviseModification> getModificationsByDeviseId(int deviseId) throws SQLException {
        return service.getModificationsByDeviseId(deviseId);
    }
    
    @Override
    public DeviseModification proposeModification(int deviseId, DeviseModification propositionData) throws SQLException {
        return service.proposeModification(deviseId, propositionData);
    }
    
    @Override
    public DeviseModification validerModification(int id, LocalDate dateValidation) throws SQLException {
        return service.validerModification(id, dateValidation);
    }
    
    @Override
    public DeviseModification refuserModification(int id) throws SQLException {
        return service.refuserModification(id);
    }
}
