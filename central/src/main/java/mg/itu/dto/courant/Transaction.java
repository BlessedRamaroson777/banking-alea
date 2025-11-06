package mg.itu.dto.courant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Transaction du module Courant
 */
public class Transaction {
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

    public Transaction(int id, Integer compteId, Integer virementId, Integer typeCode, 
                      LocalDateTime dateCreation, BigDecimal montant, Integer statutCode, Integer utilisateurId) {
        this.id = id;
        this.compteId = compteId;
        this.virementId = virementId;
        this.typeCode = typeCode;
        this.dateCreation = dateCreation;
        this.montant = montant;
        this.statutCode = statutCode;
        this.utilisateurId = utilisateurId;
    }

    public Transaction(Integer compteId, Integer virementId, Integer typeCode, 
                      LocalDateTime dateCreation, BigDecimal montant, Integer statutCode, Integer utilisateurId) {
        this.compteId = compteId;
        this.virementId = virementId;
        this.typeCode = typeCode;
        this.dateCreation = dateCreation;
        this.montant = montant;
        this.statutCode = statutCode;
        this.utilisateurId = utilisateurId;
    }

    public Integer getId() {
        return id;
    }

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
        this.montant = montant;
    }

    public Integer getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", compteId=" + compteId +
                ", virementId=" + virementId +
                ", typeCode=" + typeCode +
                ", dateCreation=" + dateCreation +
                ", montant=" + montant +
                ", statutCode=" + statutCode +
                ", utilisateurId=" + utilisateurId +
                '}';
    }
}
