package mg.itu.dto.courant;

import java.math.BigDecimal;

/**
 * DTO pour l'entité Compte du module Courant
 */
public class Compte {
    private Integer id;
    private String numero;
    private Integer typeId;
    private BigDecimal plafond;
    private BigDecimal solde;

    public Compte() {
    }

    public Compte(Integer id, String numero, Integer typeId, BigDecimal plafond, BigDecimal solde) {
        this.id = id;
        this.numero = numero;
        this.typeId = typeId;
        this.plafond = plafond;
        this.solde = solde;
    }

    public Integer getId() {
        return id;
    }

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

    public void setPlafond(BigDecimal plafond) {
        this.plafond = plafond;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", typeId=" + typeId +
                ", plafond=" + plafond +
                ", solde=" + solde +
                '}';
    }
}
