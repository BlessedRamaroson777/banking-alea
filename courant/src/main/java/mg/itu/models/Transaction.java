package mg.itu.models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mg.itu.database.utils.DB;
import mg.itu.models.base.BaseEntity;

public class Transaction extends BaseEntity<Transaction> {
    private Integer id;
    private Integer compteId;
    private Integer virementId;
    private Integer typeCode;
    private LocalDateTime dateCreation;
    private BigDecimal montant;
    private Integer statutCode;
    private Integer utilisateurId;

    public Transaction() {
    }

    public Transaction(int id, Integer compteId, Integer virementId, Integer typeCode, LocalDateTime dateCreation, BigDecimal montant, Integer statutCode, Integer utilisateurId) {
        setId(id);
        setCompteId(compteId);
        setVirementId(virementId);
        setTypeCode(typeCode);
        setDateCreation(dateCreation);
        setMontant(montant);
        setStatutCode(statutCode);
        setUtilisateurId(utilisateurId);
    }

    public Transaction(Integer compteId, Integer virementId, Integer typeCode, LocalDateTime dateCreation, BigDecimal montant, Integer statutCode, Integer utilisateurId) {
        setCompteId(compteId);
        setVirementId(virementId);
        setTypeCode(typeCode);
        setDateCreation(dateCreation);
        setMontant(montant);
        setStatutCode(statutCode);
        setUtilisateurId(utilisateurId);
    }

    public Transaction findByLibelle(String libelle) throws SQLException {
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN codes_types_transactions ctt ON t.type_code = ctt.id " +
                "WHERE ctt.libelle = ? LIMIT 1";

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.setId(rs.getInt("id"));
                    this.setCompteId(rs.getObject("compte_id", Integer.class));
                    this.setVirementId(rs.getObject("virement_id", Integer.class));
                    this.setTypeCode(rs.getInt("type_code"));
                    this.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    this.setMontant(rs.getBigDecimal("montant"));
                    this.setStatutCode(rs.getObject("statut_code", Integer.class));
                    this.setUtilisateurId(rs.getObject("utilisateur_id", Integer.class));
                    return this;
                }
            }
        }

        return null;
    }

    /**
     * Récupère toutes les transactions associées à un virement donné.
     */
    public static List<Transaction> getByVirementId(Integer virementId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE virement_id = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, virementId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setCompteId(rs.getObject("compte_id", Integer.class));
                    transaction.setVirementId(rs.getObject("virement_id", Integer.class));
                    transaction.setTypeCode(rs.getInt("type_code"));
                    transaction.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    transaction.setMontant(rs.getBigDecimal("montant"));
                    transaction.setStatutCode(rs.getObject("statut_code", Integer.class));
                    transaction.setUtilisateurId(rs.getObject("utilisateur_id", Integer.class));
                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompteId() {
        return compteId;
    }

    public void setCompteId(Integer compteId) {
        this.compteId = compteId;
    }

    public Integer getVirementId() {
        return virementId;
    }

    public void setVirementId(Integer virementId) {
        this.virementId = virementId;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        if (montant == null)
            throw new IllegalArgumentException("Le montant de la transaction est obligatoire");

        if (montant.compareTo(BigDecimal.ZERO) <= 0)
            try {
                throw new Exception("Le montant doit être positif");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        this.montant = montant;
    }

    public Integer getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(Integer statutCode) {
        if (statutCode == null)
            try {
                throw new Exception("Le statut de la transaction est obligatoire");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        this.statutCode = statutCode;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        if (utilisateurId == null)
            throw new IllegalArgumentException("Le utilisateur est obligatoire");

        this.utilisateurId = utilisateurId;
    }

    @Override
    protected String getTableName() {
        return "transactions";
    }

    @Override
    protected Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setCompteId(rs.getObject("compte_id", Integer.class));
        transaction.setVirementId(rs.getObject("virement_id", Integer.class));
        transaction.setTypeCode(rs.getInt("type_code"));
        transaction.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        transaction.setMontant(rs.getBigDecimal("montant"));
        transaction.setStatutCode(rs.getObject("statut_code", Integer.class));
        transaction.setUtilisateurId(rs.getObject("utilisateur_id", Integer.class));

        return transaction;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("compte_id", compteId);
        values.put("virement_id", virementId);
        values.put("type_code", typeCode);
        values.put("date_creation", dateCreation);
        values.put("montant", montant);
        values.put("statut_code", statutCode);
        values.put("utilisateur_id", utilisateurId);
        return values;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();
        values.put("compte_id", compteId);
        values.put("virement_id", virementId);
        values.put("type_code", typeCode);
        values.put("date_creation", dateCreation);
        values.put("montant", montant);
        values.put("statut_code", statutCode);
        values.put("utilisateur_id", utilisateurId);
        return values;
    }

    @Override
    protected void copyFrom(Transaction other) {
        this.id = other.id;
        this.compteId = other.compteId;
        this.virementId = other.virementId;
        this.typeCode = other.typeCode;
        this.dateCreation = other.dateCreation;
        this.montant = other.montant;
        this.statutCode = other.statutCode;
        this.utilisateurId = other.utilisateurId;
    }
}