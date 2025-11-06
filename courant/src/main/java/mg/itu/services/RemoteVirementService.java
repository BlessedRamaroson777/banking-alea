package mg.itu.services;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.ejb.Remote;
import mg.itu.models.ActionRole;
import mg.itu.models.Virement;

@Remote
public interface RemoteVirementService {

    List<Virement> getAll() throws Exception;

    List<Virement> getByStatut(Integer statutCode) throws Exception;

    Virement valider(Integer virementId, Integer utilisateurId, LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception;

    Virement refuser(Integer virementId, Integer utilisateurId, LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception;
}
