package mg.itu.dto.courant;

import java.math.BigDecimal;

/**
 * DTO pour l'entité ConfigFrais du module Courant
 */
public class ConfigFrais {
    private Integer id;
    private Integer typeCompteId;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal fraisMontant;
    private BigDecimal fraisPourcentage;

    public ConfigFrais() {
    }

    public ConfigFrais(int id, Integer typeCompteId, BigDecimal min, BigDecimal max, 
                      BigDecimal fraisMontant, BigDecimal fraisPourcentage) {
        this.id = id;
        this.typeCompteId = typeCompteId;
        this.min = min;
        this.max = max;
        this.fraisMontant = fraisMontant;
        this.fraisPourcentage = fraisPourcentage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeCompteId() {
        return typeCompteId;
    }

    public void setTypeCompteId(Integer typeCompteId) {
        this.typeCompteId = typeCompteId;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getFraisMontant() {
        return fraisMontant;
    }

    public void setFraisMontant(BigDecimal fraisMontant) {
        this.fraisMontant = fraisMontant;
    }

    public BigDecimal getFraisPourcentage() {
        return fraisPourcentage;
    }

    public void setFraisPourcentage(BigDecimal fraisPourcentage) {
        this.fraisPourcentage = fraisPourcentage;
    }

    public BigDecimal calculerFrais(BigDecimal montant) {
        return fraisMontant.add(montant.multiply(fraisPourcentage));
    }

    @Override
    public String toString() {
        return "ConfigFrais{" +
                "id=" + id +
                ", typeCompteId=" + typeCompteId +
                ", min=" + min +
                ", max=" + max +
                ", fraisMontant=" + fraisMontant +
                ", fraisPourcentage=" + fraisPourcentage +
                '}';
    }
}
