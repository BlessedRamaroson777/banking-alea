package mg.itu.dto.change;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Devise {
    private Integer id;
    private String code;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal cours;
    private Integer statutCode;
    private String statutLibelle;
    private LocalDate dateModification;
    
    public Devise() {
    }
    
    public Devise(Integer id, String code, LocalDate dateDebut, LocalDate dateFin, 
                     BigDecimal cours, Integer statutCode, String statutLibelle, LocalDate dateModification) {
        this.id = id;
        this.code = code;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.cours = cours;
        this.statutCode = statutCode;
        this.statutLibelle = statutLibelle;
        this.dateModification = dateModification;
    }
    
    // Getters et Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public BigDecimal getCours() {
        return cours;
    }
    
    public void setCours(BigDecimal cours) {
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
    
    public LocalDate getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDate dateModification) {
        this.dateModification = dateModification;
    }
    
    @Override
    public String toString() {
        return "Devise{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", cours=" + cours +
                ", statutCode=" + statutCode +
                ", statutLibelle='" + statutLibelle + '\'' +
                ", dateModification=" + dateModification +
                '}';
    }
}
