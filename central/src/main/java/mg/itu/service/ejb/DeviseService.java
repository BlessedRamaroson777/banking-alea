package mg.itu.service.ejb;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.dto.change.Devise;
import mg.itu.ejb.remote.DeviseRemote;
import mg.itu.utils.LookupJNDIHelper;

/**
 * Service EJB pour gérer les devises avec le service Change via EJB Remote
 */
public class DeviseService {
    private static final Logger logger = Logger.getLogger(DeviseService.class.getName());
    
    /**
     * Convertir un model Devise du serveur change vers un DTO
     */
    private Devise toDTO(mg.itu.model.Devise model) {
        if (model == null) return null;
        
        Devise dto = new Devise();
        dto.setId(model.getId());
        dto.setCode(model.getCode());
        dto.setCours(model.getCours()); // BigDecimal -> BigDecimal
        dto.setDateDebut(model.getDateDebut());
        dto.setDateFin(model.getDateFin());
        dto.setStatutCode(model.getStatutCode());
        return dto;
    }
    
    /**
     * Convertir un DTO Devise vers un model du serveur change
     */
    private mg.itu.model.Devise toModel(Devise dto) {
        if (dto == null) return null;
        
        mg.itu.model.Devise model = new mg.itu.model.Devise();
        model.setId(dto.getId());
        model.setCode(dto.getCode());
        model.setCours(dto.getCours()); // BigDecimal -> BigDecimal
        model.setDateDebut(dto.getDateDebut());
        model.setDateFin(dto.getDateFin());
        model.setStatutCode(dto.getStatutCode());
        return model;
    }
    
    /**
     * Convertir une liste de models vers une liste de DTOs
     */
    private List<Devise> toDTOList(List<mg.itu.model.Devise> models) {
        List<Devise> dtos = new ArrayList<>();
        if (models != null) {
            for (mg.itu.model.Devise model : models) {
                dtos.add(toDTO(model));
            }
        }
        return dtos;
    }
    
    /**
     * Créer une nouvelle devise via EJB
     */
    public Devise create(Devise devise) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Création d'une nouvelle devise via EJB");
            mg.itu.model.Devise model = toModel(devise);
            mg.itu.model.Devise result = deviseRemote.create(model);
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la création de la devise via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer une devise par ID via EJB
     */
    public Devise getById(int id) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération de la devise via EJB: " + id);
            mg.itu.model.Devise result = deviseRemote.getById(id);
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la devise via EJB: " + id, e);
            throw e;
        }
    }
    
    /**
     * Mettre à jour une devise via EJB
     */
    public Devise update(Devise devise) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Mise à jour de la devise via EJB: " + devise.getId());
            mg.itu.model.Devise model = toModel(devise);
            mg.itu.model.Devise result = deviseRemote.update(model);
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour de la devise via EJB", e);
            throw e;
        }
    }
    
    /**
     * Supprimer une devise via EJB
     */
    public boolean delete(int id) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Suppression de la devise via EJB: " + id);
            return deviseRemote.delete(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression de la devise via EJB: " + id, e);
            throw e;
        }
    }
    
    /**
     * Récupérer toutes les devises via EJB
     */
    public List<Devise> getAll() throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération de toutes les devises via EJB");
            List<mg.itu.model.Devise> results = deviseRemote.getAll();
            return toDTOList(results);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de toutes les devises via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises avec filtres via EJB
     */
    public List<Devise> getAllWithFilters(Map<String, Object> filters, String orderBy, Integer limit, Integer offset) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération des devises avec filtres via EJB");
            List<mg.itu.model.Devise> results = deviseRemote.getAll(filters, orderBy, limit, offset);
            return toDTOList(results);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises avec filtres via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises actives à une date donnée via EJB
     */
    public List<Devise> getActiveDevises(LocalDate date) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération des devises actives via EJB à la date: " + date);
            List<mg.itu.model.Devise> results = deviseRemote.getActiveDevises(date);
            return toDTOList(results);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises actives via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises actuellement actives via EJB
     */
    public List<Devise> getCurrentlyActiveDevises() throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération des devises actuellement actives via EJB");
            List<mg.itu.model.Devise> results = deviseRemote.getCurrentlyActiveDevises();
            return toDTOList(results);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises actives via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer une devise par code via EJB
     */
    public Devise getByCode(String code) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération de la devise via EJB avec code: " + code);
            mg.itu.model.Devise result = deviseRemote.getByCode(code);
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération de la devise via EJB avec code: " + code, e);
            throw e;
        }
    }
    
    /**
     * Récupérer les devises en attente de validation via EJB
     */
    public List<Devise> getDevisesEnAttente() throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Récupération des devises en attente via EJB");
            List<mg.itu.model.Devise> results = deviseRemote.getDevisesEnAttente();
            return toDTOList(results);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des devises en attente via EJB", e);
            throw e;
        }
    }
    
    /**
     * Valider une devise via EJB
     */
    public Devise validerCreation(int id) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Validation de la devise via EJB: " + id);
            // The remote API requires a validation date; use current date here.
            mg.itu.model.Devise result = deviseRemote.validerCreation(id, LocalDate.now());
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation de la devise via EJB: " + id, e);
            throw e;
        }
    }
    
    /**
     * Refuser une devise via EJB
     */
    public Devise refuserCreation(int id) throws Exception {
        try {
            DeviseRemote deviseRemote = LookupJNDIHelper.getDeviseService();
            logger.info("Refus de la devise via EJB: " + id);
            mg.itu.model.Devise result = deviseRemote.refuserCreation(id);
            return toDTO(result);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors du refus de la devise via EJB: " + id, e);
            throw e;
        }
    }
}
