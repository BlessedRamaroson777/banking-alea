package mg.itu.ejb;

import mg.itu.ejb.remote.CodeStatutDeviseRemote;
import mg.itu.model.CodeStatutDevise;
import mg.itu.service.CodeStatutDeviseService;
import jakarta.ejb.Stateless;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Bean EJB Stateless pour CodeStatutDevise
 */
@Stateless
public class CodeStatutDeviseEJB implements CodeStatutDeviseRemote {
    
    private final CodeStatutDeviseService service = new CodeStatutDeviseService();
    
    @Override
    public CodeStatutDevise create(CodeStatutDevise code) throws SQLException {
        return service.create(code);
    }
    
    @Override
    public CodeStatutDevise getById(int id) throws SQLException {
        return service.getById(id);
    }
    
    @Override
    public CodeStatutDevise update(CodeStatutDevise code) throws SQLException {
        return service.update(code);
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        return service.delete(id);
    }
    
    @Override
    public List<CodeStatutDevise> getAll() throws SQLException {
        return service.getAll();
    }
    
    @Override
    public List<CodeStatutDevise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return service.getAll(filters, orderBy, limit, offset);
    }
}
