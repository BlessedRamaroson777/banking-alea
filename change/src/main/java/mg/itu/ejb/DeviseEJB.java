package mg.itu.ejb;

import mg.itu.ejb.remote.DeviseRemote;
import mg.itu.model.Devise;
import mg.itu.service.DeviseService;
import jakarta.ejb.Stateless;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Bean EJB Stateless pour Devise
 */
@Stateless
public class DeviseEJB implements DeviseRemote {
    
    private final DeviseService deviseService = new DeviseService();
    
    @Override
    public Devise create(Devise devise) throws SQLException {
        return deviseService.create(devise);
    }
    
    @Override
    public Devise getById(int id) throws SQLException {
        return deviseService.getById(id);
    }
    
    @Override
    public Devise update(Devise devise) throws SQLException {
        return deviseService.update(devise);
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        return deviseService.delete(id);
    }
    
    @Override
    public List<Devise> getAll() throws SQLException {
        return deviseService.getAll();
    }
    
    @Override
    public List<Devise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException {
        return deviseService.getAll(filters, orderBy, limit, offset);
    }
    
    @Override
    public List<Devise> getActiveDevises(LocalDate date) throws SQLException {
        return deviseService.getActiveDevises(date);
    }
    
    @Override
    public List<Devise> getCurrentlyActiveDevises() throws SQLException {
        return deviseService.getCurrentlyActiveDevises();
    }
    
    @Override
    public Devise getByCode(String code) throws SQLException {
        return deviseService.getByCode(code);
    }
    
    @Override
    public List<Devise> getDevisesEnAttente() throws SQLException {
        return deviseService.getDevisesEnAttente();
    }
    
    @Override
    public Devise validerCreation(int id, LocalDate dateValidation) throws SQLException {
        return deviseService.validerCreation(id, dateValidation);
    }
    
    @Override
    public Devise refuserCreation(int id) throws SQLException {
        return deviseService.refuserCreation(id);
    }
}
