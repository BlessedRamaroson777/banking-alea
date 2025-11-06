package mg.itu.dto.courant;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Validation du module Courant
 */
public class Validation {
    private Integer id;
    private Integer transactionId;
    private Integer virementId;
    private LocalDateTime dateValidation;
    private Integer utilisateurId;
    private Integer nouveauStatutCode;
    private String typeValidation; // "TRANSACTION" ou "VIREMENT"

    public Validation() {
    }

    public Validation(Integer id, Integer transactionId, Integer virementId, LocalDateTime dateValidation,
                     Integer utilisateurId, Integer nouveauStatutCode, String typeValidation) {
        this.id = id;
        this.transactionId = transactionId;
        this.virementId = virementId;
        this.dateValidation = dateValidation;
        this.utilisateurId = utilisateurId;
        this.nouveauStatutCode = nouveauStatutCode;
        this.typeValidation = typeValidation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getVirementId() {
        return virementId;
    }

    public void setVirementId(Integer virementId) {
        this.virementId = virementId;
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

    public String getTypeValidation() {
        return typeValidation;
    }

    public void setTypeValidation(String typeValidation) {
        this.typeValidation = typeValidation;
    }

    @Override
    public String toString() {
        return "Validation{" +
                "id=" + id +
                ", transactionId=" + transactionId +
                ", virementId=" + virementId +
                ", dateValidation=" + dateValidation +
                ", utilisateurId=" + utilisateurId +
                ", nouveauStatutCode=" + nouveauStatutCode +
                ", typeValidation='" + typeValidation + '\'' +
                '}';
    }
}
