package mg.itu.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.ejb.Stateless;
import mg.itu.database.utils.DB;
import mg.itu.models.ActionRole;
import mg.itu.models.Virement;

@Stateless
public class VirementService implements RemoteVirementService {

    @Override
    public List<Virement> getAll() throws Exception {
        Connection conn = DB.getConnection();
        return Virement.findAll(conn);
    }

    @Override
    public List<Virement> getByStatut(Integer statutCode) throws Exception {
        Connection conn = DB.getConnection();
        List<Virement> allVirements = Virement.findAll(conn);
        return allVirements.stream()
                .filter(v -> v.getStatutCode() != null && v.getStatutCode().equals(statutCode))
                .toList();
    }

    @Override
    public Virement valider(Integer virementId, Integer utilisateurId, LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception {
        Connection conn = DB.getConnection();

        Virement virement = verifierVirement(conn, virementId);
        virement.valider(conn, utilisateurId, dateValidation, actionRoles);

        return virement;
    }

    @Override
    public Virement refuser(Integer virementId, Integer utilisateurId, LocalDateTime dateValidation, List<ActionRole> actionRoles) throws Exception {
        Connection conn = DB.getConnection();

        Virement virement = verifierVirement(conn, virementId);
        virement.refuser(conn, utilisateurId, dateValidation, actionRoles);

        return virement;
    }

    private Virement verifierVirement(Connection conn, Integer virementId) throws SQLException {
        Virement virement = new Virement();
        virement.setId(virementId);

        if (!virement.findById(conn))
            throw new SQLException("Le virement n'existe pas");

        return virement;
    }
}
