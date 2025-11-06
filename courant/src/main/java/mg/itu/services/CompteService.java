package mg.itu.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.ejb.Stateless;
import mg.itu.database.utils.DB;
import mg.itu.models.ActionRole;
import mg.itu.models.Compte;
import mg.itu.models.Virement;

@Stateless
public class CompteService implements RemoteCompteService {

    @Override
    public Virement virer(Integer compteEnvoyeurId,
                          Integer compteDestinataireId,
                          BigDecimal montant,
                          LocalDateTime dateCreation,
                          LocalDateTime dateEffet,
                          Integer utilisateurId,
                          Integer changeId,
                          List<ActionRole> actionRoles) throws Exception {
        try (Connection connection = DB.getConnection()) {
            Compte envoyeur = new Compte();
            envoyeur.setId(compteEnvoyeurId);

            boolean result = envoyeur.findById(connection);
            if (!result)
                throw new Exception("Le compte source ne doit pas être null");

            Compte destinataire = new Compte();
            destinataire.setId(compteDestinataireId);
            destinataire.findById(connection);

            return envoyeur.virer(connection, destinataire, montant, dateCreation, dateEffet, utilisateurId, changeId, actionRoles);
        }
    }

    @Override
    public Virement virer(Compte compteEnvoyeur, Compte compteDestinataire, BigDecimal montant, LocalDateTime dateCreation, LocalDateTime dateEffet, Integer utilisateurId, Integer changeId, List<ActionRole> actionRoles) throws Exception {
        if (compteEnvoyeur == null)
            throw new Exception("Le compte source de doit pas être null");
        try (Connection connection = DB.getConnection()) {
            return compteEnvoyeur.virer(connection, compteDestinataire,
                    montant, dateCreation, dateEffet, utilisateurId, changeId, actionRoles);
        }
    }
}
