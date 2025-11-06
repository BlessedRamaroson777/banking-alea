package mg.itu.service.ejb;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mg.itu.dto.courant.ActionRole;
import mg.itu.dto.courant.Virement;
import mg.itu.services.RemoteCompteService;
import mg.itu.utils.LookupJNDIHelper;

/**
 * Service EJB pour gérer les comptes avec le service Courant via EJB Local
 */
public class CompteService {
    private static final Logger logger = Logger.getLogger(CompteService.class.getName());
    
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
     * Effectuer un virement via EJB (avec IDs des comptes)
     */
    public Virement virer(Integer compteEnvoyeurId,
                         Integer compteDestinataireId,
                         BigDecimal montant,
                         LocalDateTime dateCreation,
                         LocalDateTime dateEffet,
                         Integer utilisateurId,
                         Integer changeId,
                         List<ActionRole> actionRoles) throws Exception {
        try {
            RemoteCompteService compteService = LookupJNDIHelper.getCompteService();
            logger.info("Virement via EJB: " + montant + " du compte " + compteEnvoyeurId + " vers " + compteDestinataireId);
            
            List<mg.itu.models.ActionRole> actionRoleModels = toActionRoleModelList(actionRoles);
            mg.itu.models.Virement result = compteService.virer(
                compteEnvoyeurId,
                compteDestinataireId,
                montant,
                dateCreation,
                dateEffet,
                utilisateurId,
                changeId,
                actionRoleModels
            );
            
            return toVirementDTO(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du virement via EJB", e);
            throw e;
        }
    }
}
