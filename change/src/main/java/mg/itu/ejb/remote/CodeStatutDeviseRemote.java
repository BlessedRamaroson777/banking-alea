package mg.itu.ejb.remote;

import mg.itu.model.CodeStatutDevise;
import jakarta.ejb.Remote;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface Remote pour le bean CodeStatutDeviseEJB
 */
@Remote
public interface CodeStatutDeviseRemote {
    
    CodeStatutDevise create(CodeStatutDevise code) throws SQLException;
    
    CodeStatutDevise getById(int id) throws SQLException;
    
    CodeStatutDevise update(CodeStatutDevise code) throws SQLException;
    
    boolean delete(int id) throws SQLException;
    
    List<CodeStatutDevise> getAll() throws SQLException;
    
    List<CodeStatutDevise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException;
}
