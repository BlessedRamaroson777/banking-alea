package mg.itu.ejb.remote;

import mg.itu.model.Devise;
import jakarta.ejb.Remote;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface Remote pour le bean DeviseEJB
 */
@Remote
public interface DeviseRemote {
    
    Devise create(Devise devise) throws SQLException;
    
    Devise getById(int id) throws SQLException;
    
    Devise update(Devise devise) throws SQLException;
    
    boolean delete(int id) throws SQLException;
    
    List<Devise> getAll() throws SQLException;
    
    List<Devise> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException;
    
    /**
     * Récupère les devises actives à une date donnée
     */
    List<Devise> getActiveDevises(LocalDate date) throws SQLException;
    
    /**
     * Récupère les devises actuellement actives
     */
    List<Devise> getCurrentlyActiveDevises() throws SQLException;
    
    /**
     * Récupère une devise par son code
     */
    Devise getByCode(String code) throws SQLException;
    
    /**
     * Récupère les devises en attente de validation
     */
    List<Devise> getDevisesEnAttente() throws SQLException;
    
    /**
     * Valide une devise créée (change le statut à actif)
     * @param dateValidation Date de validation (optionnel, null = date actuelle)
     */
    Devise validerCreation(int id, LocalDate dateValidation) throws SQLException;
    
    /**
     * Refuse une devise créée (change le statut à refusé)
     */
    Devise refuserCreation(int id) throws SQLException;
}
