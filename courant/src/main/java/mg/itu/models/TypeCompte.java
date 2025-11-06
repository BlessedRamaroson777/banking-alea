package mg.itu.models;

public class TypeCompte {
    private int id;
    private String libelle;

    public TypeCompte() {
    }

    public TypeCompte(int id, String libelle) {
        setId(id);
        setLibelle(libelle);
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
}