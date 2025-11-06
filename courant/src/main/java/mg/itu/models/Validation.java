package mg.itu.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import mg.itu.models.base.BaseEntity;

public class Validation extends BaseEntity<Validation> {
    private Integer id;
    private Integer transactionId;  // Pour validations_transactions
    private Integer virementId;     // Pour validations_virements
    private LocalDateTime dateValidation;
    private Integer utilisateurId;
    private Integer nouveauStatutCode;
    private TypeValidation typeValidation; // Détermine quelle table utiliser

    public enum TypeValidation {
        TRANSACTION("validations_transactions", "transaction_id"),
        VIREMENT("validations_virements", "virement_id");

        private final String tableName;
        private final String foreignKeyColumn;

        TypeValidation(String tableName, String foreignKeyColumn) {
            this.tableName = tableName;
            this.foreignKeyColumn = foreignKeyColumn;
        }

        public String getTableName() {
            return tableName;
        }

        public String getForeignKeyColumn() {
            return foreignKeyColumn;
        }
    }

    public Validation() {
    }

    /**
     * Constructeur pour validation de transaction
     */
    public Validation(TypeValidation typeValidation, Integer tvId, LocalDateTime dateValidation, Integer utilisateurId, Integer nouveauStatutCode) {
        if (typeValidation == TypeValidation.TRANSACTION)
            setTransactionId(tvId);
        else
            setVirementId(tvId);

        setDateValidation(dateValidation);
        setUtilisateurId(utilisateurId);
        setNouveauStatutCode(nouveauStatutCode);
        setTypeValidation(typeValidation);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
        if (transactionId != null) {
            this.typeValidation = TypeValidation.TRANSACTION;
        }
    }

    public Integer getVirementId() {
        return virementId;
    }

    public void setVirementId(Integer virementId) {
        this.virementId = virementId;
        if (virementId != null) {
            this.typeValidation = TypeValidation.VIREMENT;
        }
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Integer getNouveauStatutCode() {
        return nouveauStatutCode;
    }

    public void setNouveauStatutCode(Integer nouveauStatutCode) {
        this.nouveauStatutCode = nouveauStatutCode;
    }

    public TypeValidation getTypeValidation() {
        return typeValidation;
    }

    public void setTypeValidation(TypeValidation typeValidation) {
        this.typeValidation = typeValidation;
    }

    @Override
    protected String getTableName() {
        if (typeValidation == null) {
            throw new IllegalStateException("Type de validation non défini. Utilisez setTransactionId() ou setVirementId()");
        }
        return typeValidation.getTableName();
    }

    @Override
    protected Validation mapRow(ResultSet rs) throws SQLException {
        Validation validation = new Validation();
        validation.setId(rs.getInt("id"));
        validation.setDateValidation(rs.getTimestamp("date_validation").toLocalDateTime());
        validation.setUtilisateurId(rs.getInt("utilisateur_id"));
        validation.setNouveauStatutCode(rs.getInt("nouveau_statut_code"));

        // Déterminer le type en fonction des colonnes présentes
        try {
            validation.setTransactionId(rs.getObject("transaction_id", Integer.class));
        } catch (SQLException e) {
            // Colonne transaction_id n'existe pas, c'est une validation de virement
            validation.setVirementId(rs.getObject("virement_id", Integer.class));
        }

        return validation;
    }

    @Override
    protected LinkedHashMap<String, Object> getInsertValues() {
        if (typeValidation == null) {
            throw new IllegalStateException("Type de validation non défini");
        }

        LinkedHashMap<String, Object> values = new LinkedHashMap<>();

        // Ajouter la clé étrangère appropriée
        if (typeValidation == TypeValidation.TRANSACTION) {
            values.put("transaction_id", transactionId);
        } else {
            values.put("virement_id", virementId);
        }

        values.put("date_validation", dateValidation != null ? Timestamp.valueOf(dateValidation) : new Timestamp(System.currentTimeMillis()));
        values.put("utilisateur_id", utilisateurId);
        values.put("nouveau_statut_code", nouveauStatutCode);

        return values;
    }

    @Override
    protected LinkedHashMap<String, Object> getUpdateValues() {
        LinkedHashMap<String, Object> values = new LinkedHashMap<>();

        // On ne peut pas changer l'ID de transaction/virement dans une validation
        values.put("date_validation", dateValidation != null ? Timestamp.valueOf(dateValidation) : new Timestamp(System.currentTimeMillis()));
        values.put("utilisateur_id", utilisateurId);
        values.put("nouveau_statut_code", nouveauStatutCode);

        return values;
    }

    @Override
    protected void copyFrom(Validation other) {
        this.id = other.id;
        this.transactionId = other.transactionId;
        this.virementId = other.virementId;
        this.dateValidation = other.dateValidation;
        this.utilisateurId = other.utilisateurId;
        this.nouveauStatutCode = other.nouveauStatutCode;
        this.typeValidation = other.typeValidation;
    }
}