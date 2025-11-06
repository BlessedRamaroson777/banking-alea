package mg.itu.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.ejb.Remote;
import mg.itu.models.ActionRole;
import mg.itu.models.Compte;
import mg.itu.models.Virement;

@Remote
public interface RemoteCompteService {

    Virement virer(Integer compteEnvoyeurId,
                   Integer compteDestinataireId,
                   BigDecimal montant,
                   LocalDateTime dateCreation,
                   LocalDateTime dateEffet,
                   Integer utilisateurId,
                   Integer changeId,
                   List<ActionRole> actionRoles) throws Exception;

    Virement virer(Compte compteEnvoyeur,
                   Compte compteDestinataire,
                   BigDecimal montant,
                   LocalDateTime dateCreation,
                   LocalDateTime dateEffet,
                   Integer utilisateurId,
                   Integer changeId,
                   List<ActionRole> actionRoles) throws Exception;
}
