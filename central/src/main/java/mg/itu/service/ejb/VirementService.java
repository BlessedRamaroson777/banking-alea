package mg.itu.service.ejb;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.dto.courant.ActionRole;
import mg.itu.dto.courant.Virement;
import mg.itu.services.RemoteVirementService;
import mg.itu.utils.LookupJNDIHelper;

/**
 * Service EJB pour gérer les virements avec le service Courant via EJB Local
 */
public class VirementService {
    private static final Logger logger = Logger.getLogger(VirementService.class.getName());
    
    /**
     * Récupérer tous les virements via EJB
     */
    public List<Virement> getAll() throws Exception {
        try {
            RemoteVirementService virementService = LookupJNDIHelper.getVirementService();
            logger.info("Récupération de tous les virements via EJB");
            
            List<mg.itu.models.Virement> models = virementService.getAll();
            List<Virement> dtos = new ArrayList<>();
            for (mg.itu.models.Virement model : models) {
                dtos.add(toVirementDTO(model));
            }
            return dtos;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des virements via EJB", e);
            throw e;
        }
    }
    
    /**
     * Récupérer les virements par statut via EJB
     */
    public List<Virement> getByStatut(Integer statutCode) throws Exception {
        try {
            RemoteVirementService virementService = LookupJNDIHelper.getVirementService();
            logger.info("Récupération des virements par statut via EJB: " + statutCode);
            
            List<mg.itu.models.Virement> models = virementService.getByStatut(statutCode);
            List<Virement> dtos = new ArrayList<>();
            for (mg.itu.models.Virement model : models) {
                dtos.add(toVirementDTO(model));
            }
            return dtos;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des virements par statut via EJB", e);
            throw e;
        }
    }
    
    /**
     * Convertir un model ActionRole du serveur courant vers un DTO
     */
    private mg.itu.models.ActionRole toActionRoleModel(ActionRole dto) {
        if (dto == null) return null;
        
        mg.itu.models.ActionRole model = new mg.itu.models.ActionRole();
        model.setId(dto.getId());
        model.setNomTable(dto.getNomTable());
        model.setNomAction(dto.getNomAction());
        model.setRoleMinimum(dto.getRoleMinimum());
        return model;
    }
    
    /**
     * Convertir une liste de DTOs ActionRole vers une liste de models
     */
    private List<mg.itu.models.ActionRole> toActionRoleModelList(List<ActionRole> dtos) {
        List<mg.itu.models.ActionRole> models = new ArrayList<>();
        if (dtos != null) {
            for (ActionRole dto : dtos) {
                models.add(toActionRoleModel(dto));
            }
        }
        return models;
    }
    
    /**
     * Convertir un model Virement du serveur courant vers un DTO
     */
    private Virement toVirementDTO(mg.itu.models.Virement model) {
        if (model == null) return null;
        
        Virement dto = new Virement();
        dto.setId(model.getId());
        dto.setMontant(model.getMontant());
        dto.setDateCreation(model.getDateCreation());
        dto.setDateEffet(model.getDateEffet());
        dto.setCompteEnvoyeur(model.getCompteEnvoyeur());
        dto.setCompteDestinataire(model.getCompteDestinataire());
        dto.setUtilisateurId(model.getUtilisateurId());
        dto.setStatutCode(model.getStatutCode());
        dto.setChangeId(model.getChangeId());
        return dto;
    }
    
    /**
     * Valider un virement via EJB
     */
    public Virement valider(Integer virementId, 
                           Integer utilisateurId, 
                           LocalDateTime dateValidation, 
                           List<ActionRole> actionRoles) throws Exception {
        try {
            RemoteVirementService virementService = LookupJNDIHelper.getVirementService();
            logger.info("Validation du virement via EJB: " + virementId);
            
            List<mg.itu.models.ActionRole> actionRoleModels = toActionRoleModelList(actionRoles);
            mg.itu.models.Virement result = virementService.valider(
                virementId,
                utilisateurId,
                dateValidation,
                actionRoleModels
            );
            
            return toVirementDTO(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la validation du virement via EJB", e);
            throw e;
        }
    }
    
    /**
     * Refuser un virement via EJB
     */
    public Virement refuser(Integer virementId, 
                           Integer utilisateurId, 
                           LocalDateTime dateValidation, 
                           List<ActionRole> actionRoles) throws Exception {
        try {
            RemoteVirementService virementService = LookupJNDIHelper.getVirementService();
            logger.info("Refus du virement via EJB: " + virementId);
            
            List<mg.itu.models.ActionRole> actionRoleModels = toActionRoleModelList(actionRoles);
            mg.itu.models.Virement result = virementService.refuser(
                virementId,
                utilisateurId,
                dateValidation,
                actionRoleModels
            );
            
            return toVirementDTO(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du refus du virement via EJB", e);
            throw e;
        }
    }
}
