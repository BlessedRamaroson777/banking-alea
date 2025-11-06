package mg.itu.dto.courant;

/**
 * DTO pour l'entité Utilisateur du module Courant
 */
public class Utilisateur {
    private Integer id;
    private String nom;
    private String motDePasse;
    private int niveau;

    public Utilisateur() {
    }

    public Utilisateur(Integer id, String nom, String motDePasse, int niveau) {
        this.id = id;
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.niveau = niveau;
    }

    public Utilisateur(String nom, String motDePasse) {
        this.nom = nom;
        this.motDePasse = motDePasse;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", niveau=" + niveau +
                '}';
    }
}
