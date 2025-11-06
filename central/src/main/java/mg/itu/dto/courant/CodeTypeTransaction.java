package mg.itu.dto.courant;

/**
 * DTO pour l'entité CodeTypeTransaction du module Courant
 */
public class CodeTypeTransaction {
    private Integer id;
    private String libelle;

    public CodeTypeTransaction() {
    }

    public CodeTypeTransaction(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public CodeTypeTransaction(String libelle) {
        this.libelle = libelle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        return "CodeTypeTransaction{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
