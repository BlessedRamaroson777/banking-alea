package mg.itu.service;

import mg.itu.model.CodeStatutDevise;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service pour CodeStatutDevise
 */
public class CodeStatutDeviseService {
    
    public CodeStatutDevise create(CodeStatutDevise code) throws SQLException {
        return code.create();
    }
    
    public CodeStatutDevise getById(int id) throws SQLException {
        CodeStatutDevise code = CodeStatutDevise.getById(CodeStatutDevise.class, id);
        if (code == null) {
            throw new SQLException("Code statut devise non trouvé avec l'ID: " + id);
        }
        return code;
    }
    
    public CodeStatutDevise update(CodeStatutDevise code) throws SQLException {
        return code.update();
    }
    
    public boolean delete(int id) throws SQLException {
        return CodeStatutDevise.deleteById(CodeStatutDevise.class, id);
    }
    
    public List<CodeStatutDevise> getAll() throws SQLException {
        return CodeStatutDevise.getAll(CodeStatutDevise.class);
    }
    
    public List<CodeStatutDevise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return CodeStatutDevise.getAll(CodeStatutDevise.class, filters, orderBy, limit, offset);
    }
}
