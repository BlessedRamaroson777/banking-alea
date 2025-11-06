package mg.itu.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import mg.itu.models.base.BaseEntity;

public class Compte extends BaseEntity<Compte> {
    private Integer id;
    private String numero;
    private Integer typeId;
    private BigDecimal plafond;
    private BigDecimal solde;

    public Compte() {
    }

    @Override
    protected void copyFrom(Compte other) {
        if (other == null) return;
        this.id = other.id;
        this.numero = other.numero;
        this.typeId = other.typeId;
        this.plafond = other.plafond;
        this.solde = other.solde;
    }

    public Compte(Integer id, String numero, Integer typeId, BigDecimal plafond, BigDecimal solde) {
        setId(id);
        setNumero(numero);
        setPlafond(plafond);
        setSolde(solde);
        setTypeId(typeId);
    }

    public boolean plafondEstAtteint(Connection conn) throws SQLException {
        if (this.plafond == null || this.plafond.compareTo(BigDecimal.ZERO) <= 0)
            return false;

        CodeTypeTransaction typeDebit = new CodeTypeTransaction();
        typeDebit.setLibelle("DEBIT");
        typeDebit.findByLibelle();

        String sql = """
                SELECT COALESCE(SUM(t.montant), 0) AS total
                FROM transactions t
                WHERE t.compte_id = ?
                  AND t.type_code = ?
                  AND DATE(t.date_creation) = CURRENT_DATE
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, this.id);
            ps.setInt(2, typeDebit.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    if (total == null) total = BigDecimal.ZERO;

                    return total.compareTo(this.plafond) >= 0;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        return false;
    }

    public Virement virer(Connection conn, Compte destinataire, BigDecimal montant, LocalDateTime dateCreation,
                          LocalDateTime dateEffet, Integer utilisateurId, Integer changeId, List<ActionRole> actionRoles) throws Exception {
        if (this.solde.compareTo(montant) < 0)
            throw new Exception("Solde insuffisant pour le virement");

        if (plafondEstAtteint(conn))
            throw new Exception("Le plafond journalier a déjà été atteint pour ce compte");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurId);
        utilisateur.findById(conn);

        if (!utilisateur.roleSuffisant(actionRoles, "Virement", "CREER"))
            throw new Exception("Vous n'avez pas suffisamment de droit pour cette ation");

        // sauvegarde du virement. statut 1: en attente
        Virement virement = new Virement(montant, dateCreation, dateEffet, this.id, destinataire.id, utilisateurId, 1, changeId);
        virement.save(conn);

        // Utiliser exactement la date de création du virement (non nulle)
        LocalDateTime txDate = virement.getDateCreation();

        // sauvegarde des transactions
        CodeTypeTransaction typeDebit = new CodeTypeTransaction();
        typeDebit.setLibelle("DEBIT");
        typeDebit.findByLibelle();

        CodeTypeTransaction typeCredit = new CodeTypeTransaction();
        typeCredit.setLibelle("CREDIT");
        typeCredit.findByLibelle();

        BigDecimal frais = new ConfigFrais()
                .getByTypeCompteAndMontant(this.getTypeId(), montant)
                .calculerFrais(montant);

        Transaction transCredit = new Transaction(destinataire.id, virement.getId(),
                typeCredit.getId(), txDate,
                montant.subtract(frais), 1, utilisateurId);

        Transaction transDebit = new Transaction(this.id, virement.getId(),
                typeDebit.getId(), txDate,
                montant.subtract(frais), 1, utilisateurId);

        Transaction transFrais = new Transaction(this.id, virement.getId(),
                typeDebit.getId(), txDate,
                frais, 1, utilisateurId);

        transCredit.save(conn);
        transDebit.save(conn);
        transFrais.save(conn);

        return virement;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        if (solde == null)
            throw new IllegalArgumentException("Le solde ne doit pas être null");

        if (solde.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le solde doit être positif");

        this.solde = solde;
    }

    public void setPlafond(BigDecimal plafond) {
        if (plafond != null && plafond.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le plafond doit être une valeur positive");

        this.plafond = plafond;
    }

    @Override
    protected String getTableName() {
        return "comptes";
    }

    @Override
    protected Compte mapRow(ResultSet rs) throws SQLException {
        Compte c = new Compte();
        c.setId(rs.getInt("id"));
        c.setNumero(rs.getString("numero"));
        c.setTypeId(rs.getInt("type_id"));
        c.setPlafond(rs.getBigDecimal("plafond"));
        c.setSolde(rs.getBigDecimal("solde"));

        return c;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("numero", this.numero);
        map.put("type_id", this.typeId);
        map.put("plafond", this.plafond);
        map.put("solde", this.solde);

        return map;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("numero", this.numero);
        map.put("type_id", this.typeId);
        map.put("plafond", this.plafond);
        map.put("solde", this.solde);

        return map;
    }

    public static Optional<Compte> findById(Connection conn, int id) throws SQLException {
        return BaseEntity.findById(conn, Compte::new, id);
    }

    public static List<Compte> findAll(Connection conn) throws SQLException {
        return BaseEntity.findAll(conn, Compte::new);
    }
}