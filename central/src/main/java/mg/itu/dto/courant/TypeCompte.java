package mg.itu.dto.courant;

/**
 * DTO pour l'entité TypeCompte du module Courant
 */
public class TypeCompte {
    private int id;
    private String libelle;

    public TypeCompte() {
    }

    public TypeCompte(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "TypeCompte{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
