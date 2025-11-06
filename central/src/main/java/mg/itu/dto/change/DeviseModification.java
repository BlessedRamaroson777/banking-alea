package mg.itu.dto.change;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeviseModification {
    private Integer id;
    private Integer deviseId;
    private String code;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double cours;
    private Integer statutCode;
    private String statutLibelle;
    private Integer statutValidation;
    private LocalDateTime dateProposition;
    private LocalDateTime dateTraitement;
    
    public DeviseModification() {
    }
    
    public DeviseModification(Integer id, Integer deviseId, String code, LocalDate dateDebut, 
                             LocalDate dateFin, Double cours, Integer statutCode, String statutLibelle,
                             Integer statutValidation, LocalDateTime dateProposition, LocalDateTime dateTraitement) {
        this.id = id;
        this.deviseId = deviseId;
        this.code = code;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.cours = cours;
        this.statutCode = statutCode;
        this.statutLibelle = statutLibelle;
        this.statutValidation = statutValidation;
        this.dateProposition = dateProposition;
        this.dateTraitement = dateTraitement;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getDeviseId() {
        return deviseId;
    }
    
    public void setDeviseId(Integer deviseId) {
        this.deviseId = deviseId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public Double getCours() {
        return cours;
    }
    
    public void setCours(Double cours) {
        this.cours = cours;
    }
    
    public Integer getStatutCode() {
        return statutCode;
    }
    
    public void setStatutCode(Integer statutCode) {
        this.statutCode = statutCode;
    }
    
    public String getStatutLibelle() {
        return statutLibelle;
    }
    
    public void setStatutLibelle(String statutLibelle) {
        this.statutLibelle = statutLibelle;
    }
    
    public Integer getStatutValidation() {
        return statutValidation;
    }
    
    public void setStatutValidation(Integer statutValidation) {
        this.statutValidation = statutValidation;
    }
    
    public LocalDateTime getDateProposition() {
        return dateProposition;
    }
    
    public void setDateProposition(LocalDateTime dateProposition) {
        this.dateProposition = dateProposition;
    }
    
    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }
    
    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
    
    @Override
    public String toString() {
        return "DeviseModification{" +
                "id=" + id +
                ", deviseId=" + deviseId +
                ", code='" + code + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", cours=" + cours +
                ", statutCode=" + statutCode +
                ", statutLibelle='" + statutLibelle + '\'' +
                ", statutValidation=" + statutValidation +
                ", dateProposition=" + dateProposition +
                ", dateTraitement=" + dateTraitement +
                '}';
    }
}
