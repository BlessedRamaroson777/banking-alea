package mg.itu.ejb.remote;

import mg.itu.model.DeviseModification;
import jakarta.ejb.Remote;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface Remote pour DeviseModification EJB
 */
@Remote
public interface DeviseModificationRemote {
    
    /**
     * Créer une proposition de modification
     */
    DeviseModification create(DeviseModification modification) throws SQLException;
    
    /**
     * Récupérer une proposition par ID
     */
    DeviseModification getById(int id) throws SQLException;
    
    /**
     * Mettre à jour une proposition
     */
    DeviseModification update(DeviseModification modification) throws SQLException;
    
    /**
     * Supprimer une proposition
     */
    boolean delete(int id) throws SQLException;
    
    /**
     * Récupérer toutes les propositions avec filtres
     */
    List<DeviseModification> getAll(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws SQLException;
    
    /**
     * Récupérer toutes les propositions
     */
    List<DeviseModification> getAll() throws SQLException;
    
    /**
     * Récupérer toutes les propositions en attente
     */
    List<DeviseModification> getModificationsEnAttente() throws SQLException;
    
    /**
     * Récupérer toutes les propositions pour une devise
     */
    List<DeviseModification> getModificationsByDeviseId(int deviseId) throws SQLException;
    
    /**
     * Proposer une modification pour une devise
     */
    DeviseModification proposeModification(int deviseId, DeviseModification propositionData) throws SQLException;
    
    /**
     * Valider une proposition de modification
     * @param dateValidation Date de validation à appliquer (optionnel)
     */
    DeviseModification validerModification(int id, LocalDate dateValidation) throws SQLException;
    
    /**
     * Refuser une proposition de modification
     */
    DeviseModification refuserModification(int id) throws SQLException;
}
