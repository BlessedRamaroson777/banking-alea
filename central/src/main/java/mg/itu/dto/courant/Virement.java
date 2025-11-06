package mg.itu.dto.courant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Virement du module Courant
 */
public class Virement {
    private Integer id;
    private BigDecimal montant;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEffet;
    private Integer compteEnvoyeur;
    private Integer compteDestinataire;
    private Integer utilisateurId;
    private Integer statutCode;
    private Integer changeId;

    public Virement() {
    }

    public Virement(Integer id, BigDecimal montant, LocalDateTime dateCreation,
                    LocalDateTime dateEffet, Integer compteEnvoyeur,
                    Integer compteDestinataire, Integer utilisateurId,
                    Integer statutCode, Integer changeId) {
        this.id = id;
        this.montant = montant;
        this.dateCreation = dateCreation;
        this.dateEffet = dateEffet;
        this.compteEnvoyeur = compteEnvoyeur;
        this.compteDestinataire = compteDestinataire;
        this.utilisateurId = utilisateurId;
        this.statutCode = statutCode;
        this.changeId = changeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDateTime dateEffet) {
        this.dateEffet = dateEffet;
    }

    public Integer getCompteEnvoyeur() {
        return compteEnvoyeur;
    }

    public void setCompteEnvoyeur(Integer compteEnvoyeur) {
        this.compteEnvoyeur = compteEnvoyeur;
    }

    public Integer getCompteDestinataire() {
        return compteDestinataire;
    }

    public void setCompteDestinataire(Integer compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Integer getStatutCode() {
        return statutCode;
    }

    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }

    @Override
    public String toString() {
        return "Virement{" +
                "id=" + id +
                ", montant=" + montant +
                ", dateCreation=" + dateCreation +
                ", dateEffet=" + dateEffet +
                ", compteEnvoyeur=" + compteEnvoyeur +
                ", compteDestinataire=" + compteDestinataire +
                ", utilisateurId=" + utilisateurId +
                ", statutCode=" + statutCode +
                ", changeId=" + changeId +
                '}';
    }
}
